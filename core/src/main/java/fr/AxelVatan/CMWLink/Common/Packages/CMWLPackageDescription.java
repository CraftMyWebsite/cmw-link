package fr.AxelVatan.CMWLink.Common.Packages;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CMWLPackageDescription{

    private String name;
    private String route_prefix;
    private String sp_main;
    private String bg_main;
    private String version;
    private String author;
    private Set<String> depends = new HashSet<>();
    private File file = null;
    
}