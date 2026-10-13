package pcshop.subsystem;

import pcshop.model.OperatingSystem;
import pcshop.model.PartType;

// SUBSYSTEM CLASS #6 - installs the operating system on an assembled PC.
public class OsInstaller {

    public void install(OperatingSystem operatingSystem, AssembledPc pc) {
        if (pc.installedOs().isPresent()) {
            throw new IllegalStateException(pc.serialNumber() + " already has an OS installed.");
        }
        boolean hasDrive = pc.parts().stream().anyMatch(part -> part.type() == PartType.STORAGE);
        if (!hasDrive) {
            throw new IllegalStateException(pc.serialNumber() + " has no drive to install an OS on.");
        }
        pc.markOsInstalled(operatingSystem);
    }
}
