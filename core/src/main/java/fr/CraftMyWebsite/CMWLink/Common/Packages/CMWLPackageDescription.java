package fr.CraftMyWebsite.CMWLink.Common.Packages;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CMWLPackageDescription {

    private static final ThreadLocal<Yaml> YAML = new ThreadLocal<Yaml>() {
        @Override
        @NotNull
        protected Yaml initialValue() {
            return new Yaml(new SafeConstructor(new LoaderOptions()) {
                {
                    yamlConstructors.put(null, new AbstractConstruct() {
                        @NotNull
                        @Override
                        public Object construct(@NotNull final Node node) {
                            // Return the node as a string if it's an unknown tag
                            return node.toString();
                        }
                    });
                }
            });
        }
    };

    private String name;
    private String route_prefix;
    private String sp_main;
    private String bg_main;
    private String vl_main;
    private String version;
    private String author;
    private @NotNull List<String> depends = new ArrayList<>();
    private File file = null;

    public CMWLPackageDescription(@NotNull final InputStream stream) {
        loadMap(asMap(YAML.get().load(stream)));
    }

    private void loadMap(@NotNull Map<?, ?> map) {
        name = map.get("name").toString();
        route_prefix = map.get("route_prefix").toString();
        version = map.get("version").toString();
        author = map.get("author").toString();
        if (map.get("sp_main") != null) {
            sp_main = map.get("sp_main").toString();
        }
        if (map.get("bg_main") != null) {
            bg_main = map.get("bg_main").toString();
        }
        if (map.get("vl_main") != null) {
            vl_main = map.get("vl_main").toString();
        }
        depends = makePluginNameList(map, "depends");
    }

    @NotNull
    private static List<String> makePluginNameList(@NotNull final Map<?, ?> map, @NotNull final String key) {
        final Object value = map.get(key);
        if (value == null) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<String> builder = ImmutableList.<String>builder();
        try {
            for (final Object entry : (Iterable<?>) value) {
                builder.add(entry.toString().replace(' ', '_'));
            }
        } catch (ClassCastException ex) {
            // Handle exception if necessary
        } catch (NullPointerException ex) {
            // Handle exception if necessary
        }
        return builder.build();
    }

    @NotNull
    private Map<?, ?> asMap(@NotNull Object object) {
        if (object instanceof Map) {
            return (Map<?, ?>) object;
        }
        return Map.of();
    }
}
