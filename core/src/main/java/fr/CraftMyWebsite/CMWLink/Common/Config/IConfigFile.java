package fr.CraftMyWebsite.CMWLink.Common.Config;

import lombok.Getter;

import java.io.File;
import java.util.logging.Logger;

public class IConfigFile {
    private @Getter File filePath;
    private @Getter Logger log;

    public IConfigFile(File filePath, Logger log) {
        this.filePath = filePath;
        this.log = log;
    }
}
