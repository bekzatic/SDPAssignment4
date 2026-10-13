package pcshop.subsystem;

import pcshop.model.OperatingSystem;
import pcshop.model.Part;

import java.util.List;
import java.util.Optional;

// A physical computer that left the assembly bench. Created by
// AssemblyWorkshop, gets its OS from OsInstaller, checked by BurnInTester.
public class AssembledPc {

    private final String serialNumber;
    private final List<Part> parts;
    private OperatingSystem installedOs;

    AssembledPc(String serialNumber, List<Part> parts) {
        this.serialNumber = serialNumber;
        this.parts = List.copyOf(parts);
    }

    public String serialNumber() {
        return serialNumber;
    }

    public List<Part> parts() {
        return parts;
    }

    public Optional<OperatingSystem> installedOs() {
        return Optional.ofNullable(installedOs);
    }

    void markOsInstalled(OperatingSystem operatingSystem) {
        this.installedOs = operatingSystem;
    }
}
