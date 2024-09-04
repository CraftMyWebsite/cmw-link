package fr.CraftMyWebsite.CMWLink.Common.Packages;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginAwareness;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CMWLPackageDescription{

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
							if (!node.getTag().startsWith("!@")) {
								// Unknown tag - will fail
								return SafeConstructor.undefinedConstructor.construct(node);
							}
							// Unknown awareness - provide a graceful substitution
							return new PluginAwareness() {
								@Override
								public String toString() {
									return node.toString();
								}
							};
						}
					});
					for (final PluginAwareness.Flags flag : PluginAwareness.Flags.values()) {
						yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
							@NotNull
							@Override
							public PluginAwareness.Flags construct(@NotNull final Node node) {
								return flag;
							}
						});
					}
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

	public CMWLPackageDescription(@NotNull final InputStream stream) throws InvalidDescriptionException {
		loadMap(asMap(YAML.get().load(stream)));
	}

	private void loadMap(@NotNull Map<?, ?> map) throws InvalidDescriptionException {
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
    private static List<String> makePluginNameList(@NotNull final Map<?, ?> map, @NotNull final String key) throws InvalidDescriptionException {
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
            throw new InvalidDescriptionException(ex, key + " is of wrong type");
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "invalid " + key + " format");
        }
        return builder.build();
    }

	@NotNull
	private Map<?, ?> asMap(@NotNull Object object) throws InvalidDescriptionException {
		if (object instanceof Map) {
			return (Map<?, ?>) object;
		}
		throw new InvalidDescriptionException("Plugin description file is empty or not properly structured. Is " + object + "but should be a map.");
	}
}