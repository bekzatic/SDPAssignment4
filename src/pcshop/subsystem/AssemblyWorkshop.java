package pcshop.subsystem;

import pcshop.model.Part;

import java.util.List;

// SUBSYSTEM CLASS #5 - puts reserved parts together and gives the new
// computer a serial number. It trusts the parts it gets: checking them is
// CompatibilityChecker's job.
public class AssemblyWorkshop {

    private int nextSerial = 1;

    public AssembledPc assemble(Reservation reservation) {
        String serialNumber = String.format("PC-2026-%04d", nextSerial++);
        return new AssembledPc(serialNumber, reservation.parts());
    }
}
