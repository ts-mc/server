package dev.theysix.tsstaff;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class TSStaffUpdates implements CommandExecutor, Listener {
    private static final int PAGE_SIZE = 45;
    private static final long MAX_STARTUP_LOG_BYTES = 8L * 1024L * 1024L;
    private static final Pattern JSON_STRING = Pattern.compile("\\\"%s\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"");
    private static final Pattern PLUGIN_LOAD_VERSION = Pattern.compile(
            "\\[([^\\]]+)]\\s+(?:Loading|Enabling)\\s+(?:server plugin\\s+)?(.+?)\\s+v([^\\s]+)",
            Pattern.CASE_INSENSITIVE);
    private final JavaPlugin plugin;
    private final ExecutorService workers = Executors.newFixedThreadPool(4, task -> {
        Thread thread = new Thread(task, "TSStaff-update-check");
        thread.setDaemon(true);
        return thread;
    });
    private final ConcurrentHashMap<UUID, Long> requestIds = new ConcurrentHashMap<>();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public TSStaffUpdates(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tsstaff.update")) {
            sender.sendMessage(message("&cYou don't have permission."));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(message("&cOpen /update in game."));
            return true;
        }

        long requestId = System.nanoTime();
        requestIds.put(player.getUniqueId(), requestId);
        player.sendMessage(message("&7Checking plugin versions from the startup log and metadata against trusted sources..."));
        Map<String, String> loggedVersions = readStartupLogVersions();
        List<PluginSnapshot> plugins = new ArrayList<>();
        for (Plugin installed : Bukkit.getPluginManager().getPlugins()) {
            PluginDescriptionFile description = installed.getDescription();
            String name = description.getName();
            String base = "updates.sources." + name + ".";
            String loggedVersion = loggedVersions.get(normalizePluginName(name));
            String currentVersion = loggedVersion == null ? description.getVersion() : loggedVersion;
            plugins.add(new PluginSnapshot(name, currentVersion, loggedVersion != null,
                    plugin.getConfig().getString(base + "provider", ""),
                    plugin.getConfig().getString(base + "project", ""),
                    plugin.getConfig().getString(base + "url", "")));
        }
        CompletableFuture<?>[] checks = plugins.stream()
                .map(snapshot -> CompletableFuture.runAsync(() -> snapshot.check(), workers))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(checks).whenComplete((ignored, failure) -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline() || requestIds.getOrDefault(player.getUniqueId(), -1L) != requestId) return;
            plugins.sort(Comparator.comparing(item -> item.name.toLowerCase(Locale.ROOT)));
            openPage(player, plugins, 0);
        }));
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof UpdatePage page)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();
        if (slot == 45 && page.page > 0) openPage(player, page.plugins, page.page - 1);
        else if (slot == 53 && page.page + 1 < page.pageCount()) openPage(player, page.plugins, page.page + 1);
        else if (slot == 49) player.performCommand("update");
    }

    public void shutdown() {
        workers.shutdownNow();
    }

    private void openPage(Player player, List<PluginSnapshot> plugins, int pageNumber) {
        UpdatePage holder = new UpdatePage(plugins, pageNumber);
        Inventory inventory = Bukkit.createInventory(holder, 54,
                color("&8Plugin updates &7(" + (pageNumber + 1) + "/" + holder.pageCount() + ")"));
        holder.inventory = inventory;
        int start = pageNumber * PAGE_SIZE;
        for (int index = start; index < Math.min(start + PAGE_SIZE, plugins.size()); index++) {
            PluginSnapshot snapshot = plugins.get(index);
            inventory.setItem(index - start, item(snapshot));
        }
        if (pageNumber > 0) inventory.setItem(45, button(Material.ARROW, "&bPrevious page"));
        inventory.setItem(49, button(Material.CLOCK, "&eRefresh update checks"));
        if (pageNumber + 1 < holder.pageCount()) inventory.setItem(53, button(Material.ARROW, "&bNext page"));
        player.openInventory(inventory);
    }

    private ItemStack item(PluginSnapshot snapshot) {
        Material material;
        String state;
        String color;
        switch (snapshot.status) {
            case CURRENT -> { material = Material.LIME_DYE; state = "CURRENT"; color = "&a"; }
            case AVAILABLE -> { material = Material.GOLD_INGOT; state = "UPDATE AVAILABLE"; color = "&e"; }
            case NEWER_LOCAL -> { material = Material.PAPER; state = "LOCAL VERSION IS NEWER — REVIEW"; color = "&b"; }
            case UNKNOWN -> { material = Material.GRAY_DYE; state = "NO AUTOMATED SOURCE"; color = "&7"; }
            default -> { material = Material.BARRIER; state = "CHECK FAILED — REVIEW"; color = "&c"; }
        }
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(color("&f" + snapshot.name));
        List<String> lore = new ArrayList<>();
        lore.add(color("&7Installed: &f" + snapshot.current));
        lore.add(color(snapshot.fromStartupLog ? "&7Detected via: &flogs/latest.log" : "&7Detected via: &fplugin metadata"));
        lore.add(color("&7Status: " + color + state));
        if (!snapshot.latest.isEmpty()) lore.add(color("&7Latest stable: &f" + snapshot.latest));
        if (!snapshot.url.isEmpty()) lore.add(color("&7Source: &b" + snapshot.url));
        if (snapshot.status == Status.UNKNOWN) lore.add(color("&8Custom/private or source not configured"));
        lore.add(color("&8This command checks only; it never installs updates."));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack button(Material material, String title) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(title));
        item.setItemMeta(meta);
        return item;
    }

    private Map<String, String> readStartupLogVersions() {
        try {
            Path worldPath = plugin.getServer().getWorldContainer().toPath();
            Path serverRoot = worldPath.getParent();
            if (serverRoot == null) {
                File pluginFolder = plugin.getDataFolder().getParentFile();
                if (pluginFolder == null || pluginFolder.getParentFile() == null) return Map.of();
                serverRoot = pluginFolder.getParentFile().toPath();
            }
            Path startupLog = serverRoot.resolve("logs").resolve("latest.log");
            if (!Files.isRegularFile(startupLog) || Files.size(startupLog) > MAX_STARTUP_LOG_BYTES) return Map.of();

            Map<String, String> versions = new HashMap<>();
            try (var lines = Files.lines(startupLog, StandardCharsets.UTF_8)) {
                lines.forEach(line -> {
                    Matcher matcher = PLUGIN_LOAD_VERSION.matcher(line);
                    if (!matcher.find()) return;
                    String name = matcher.group(2).trim();
                    String version = matcher.group(3).replaceAll("[,;]$", "");
                    if (!name.isBlank() && !version.isBlank()) versions.put(normalizePluginName(name), version);
                });
            }
            return versions;
        } catch (IOException | SecurityException ignored) {
            return Map.of();
        }
    }

    private static String normalizePluginName(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private String message(String text) {
        return color(plugin.getConfig().getString("prefix", "&bTSStaff &7| &r") + text);
    }

    private static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private String getLatest(String provider, String project) throws IOException, InterruptedException {
        String endpoint;
        if (provider.equalsIgnoreCase("modrinth")) {
            String loaders = URLEncoder.encode("[\"paper\",\"spigot\",\"bukkit\"]", StandardCharsets.UTF_8);
            String minecraftVersion = plugin.getConfig().getString("updates.minecraft-version", "1.21.11");
            String versions = URLEncoder.encode("[\"" + minecraftVersion + "\"]", StandardCharsets.UTF_8);
            endpoint = "https://api.modrinth.com/v2/project/" + project + "/version?loaders=" + loaders
                    + "&game_versions=" + versions + "&include_changelog=false";
            return latestModrinth(request(endpoint));
        } else if (provider.equalsIgnoreCase("hangar")) {
            endpoint = "https://hangar.papermc.io/api/v1/projects/" + project + "/versions?platform=PAPER&limit=100";
            return latestHangar(request(endpoint));
        } else if (provider.equalsIgnoreCase("github")) {
            endpoint = "https://api.github.com/repos/" + project + "/releases/latest";
        } else {
            throw new IOException("Unsupported provider");
        }
        return firstString(request(endpoint), "tag_name");
    }

    private String request(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .timeout(Duration.ofSeconds(7))
                .header("User-Agent", "TheySix-TSStaff-UpdateCheck/1.3.0")
                .header("Accept", "application/vnd.github+json")
                .GET().build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Source returned HTTP " + response.statusCode());
        return response.body();
    }

    private static String latestModrinth(String json) throws IOException {
        for (String object : jsonObjects(json)) {
            if (!"release".equals(firstStringOrEmpty(object, "version_type"))) continue;
            String version = firstStringOrEmpty(object, "version_number");
            if (!version.isBlank()) return version;
        }
        throw new IOException("No stable Paper-compatible version found");
    }

    private String latestHangar(String json) throws IOException {
        String minecraftVersion = plugin.getConfig().getString("updates.minecraft-version", "1.21.11");
        for (String object : jsonObjects(json)) {
            Matcher channel = Pattern.compile("\\\"channel\\\"\\s*:\\s*\\{[^}]*\\\"name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"").matcher(object);
            if (!channel.find() || !"Release".equals(channel.group(1))) continue;
            Matcher platform = Pattern.compile("\\\"PAPER\\\"\\s*:\\s*\\[([^]]*)\\]").matcher(object);
            if (!platform.find() || !platform.group(1).contains("\"" + minecraftVersion + "\"")) continue;
            String version = firstStringOrEmpty(object, "name");
            if (!version.isBlank()) return version;
        }
        throw new IOException("No stable Paper-compatible version found");
    }

    private static String firstString(String json, String key) throws IOException {
        String value = firstStringOrEmpty(json, key);
        if (value.isBlank()) throw new IOException("No version tag found");
        return value;
    }

    private static String firstStringOrEmpty(String json, String key) {
        Matcher matcher = Pattern.compile(String.format(JSON_STRING.pattern(), Pattern.quote(key))).matcher(json);
        return matcher.find() ? matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\") : "";
    }

    private static List<String> jsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        Deque<Integer> starts = new ArrayDeque<>();
        boolean quoted = false;
        boolean escaped = false;
        for (int index = 0; index < json.length(); index++) {
            char current = json.charAt(index);
            if (quoted) {
                if (escaped) escaped = false;
                else if (current == '\\') escaped = true;
                else if (current == '"') quoted = false;
                continue;
            }
            if (current == '"') quoted = true;
            else if (current == '{') starts.push(index);
            else if (current == '}' && !starts.isEmpty()) objects.add(json.substring(starts.pop(), index + 1));
        }
        return objects;
    }

    private enum Status { CURRENT, AVAILABLE, NEWER_LOCAL, UNKNOWN, FAILED }

    private final class PluginSnapshot {
        private final String name;
        private final String current;
        private final boolean fromStartupLog;
        private final String provider;
        private final String project;
        private final String url;
        private volatile String latest = "";
        private volatile Status status = Status.UNKNOWN;

        private PluginSnapshot(String name, String current, boolean fromStartupLog, String provider, String project, String url) {
            this.name = name;
            this.current = current;
            this.fromStartupLog = fromStartupLog;
            this.provider = provider;
            this.project = project;
            this.url = url;
            if (!provider.isBlank() && !project.isBlank()) status = Status.FAILED;
        }

        private void check() {
            if (provider.isBlank() || project.isBlank()) return;
            try {
                latest = getLatest(provider, project);
                int comparison = compareVersions(current, latest);
                status = comparison == 0 ? Status.CURRENT : comparison < 0 ? Status.AVAILABLE : Status.NEWER_LOCAL;
            } catch (Exception ignored) {
                status = Status.FAILED;
            }
        }
    }

    private static int compareVersions(String left, String right) {
        String[] a = left.toLowerCase(Locale.ROOT).replaceFirst("^[v=]", "").split("[.+_-]", -1);
        String[] b = right.toLowerCase(Locale.ROOT).replaceFirst("^[v=]", "").split("[.+_-]", -1);
        for (int index = 0; index < Math.max(a.length, b.length); index++) {
            String x = index < a.length ? a[index] : "0";
            String y = index < b.length ? b[index] : "0";
            if (x.matches("\\d+") && y.matches("\\d+")) {
                int result;
                try { result = Long.compare(Long.parseLong(x), Long.parseLong(y)); }
                catch (NumberFormatException ignored) { result = x.compareTo(y); }
                if (result != 0) return result;
            } else {
                int result = x.compareTo(y);
                if (result != 0) return result;
            }
        }
        return 0;
    }

    private static final class UpdatePage implements InventoryHolder {
        private final List<PluginSnapshot> plugins;
        private final int page;
        private Inventory inventory;

        private UpdatePage(List<PluginSnapshot> plugins, int page) {
            this.plugins = plugins;
            this.page = page;
        }

        private int pageCount() {
            return Math.max(1, (plugins.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
