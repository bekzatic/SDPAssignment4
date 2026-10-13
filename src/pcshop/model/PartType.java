package pcshop.model;

// Kinds of parts the shop sells. An enum instead of free-form strings, so a
// typo like "Cpu" can't sneak into the catalog.
public enum PartType {
    CPU,
    MOTHERBOARD,
    RAM,
    GPU,
    STORAGE,
    PSU,
    CASE
}
