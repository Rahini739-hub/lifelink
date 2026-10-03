package com.example.data

enum class BloodGroup(val label: String) {
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-");

    companion object {
        val ALL_LABELS: List<String> = entries.map { it.label }

        fun fromLabel(label: String): BloodGroup? {
            return entries.firstOrNull { it.label.equals(label.trim(), ignoreCase = true) }
        }

        /**
         * Medical Red Blood Cell (RBC) Compatibility Matrix:
         * Returns true if a donor with [donorGroup] can safely donate RBCs to a patient needing [recipientGroup].
         */
        fun isCompatibleDonor(donorGroup: String, recipientGroup: String): Boolean {
            val donor = donorGroup.trim().uppercase()
            val recipient = recipientGroup.trim().uppercase()
            val compatibleRecipients: Set<String> = when (donor) {
                "O-" -> setOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
                "O+" -> setOf("O+", "A+", "B+", "AB+")
                "A-" -> setOf("A-", "A+", "AB-", "AB+")
                "A+" -> setOf("A+", "AB+")
                "B-" -> setOf("B-", "B+", "AB-", "AB+")
                "B+" -> setOf("B+", "AB+")
                "AB-" -> setOf("AB-", "AB+")
                "AB+" -> setOf("AB+")
                else -> emptySet()
            }
            return recipient in compatibleRecipients
        }

        /**
         * Returns all donor blood groups that are medically compatible for a patient with [recipientGroup].
         */
        fun getCompatibleDonorGroupsFor(recipientGroup: String): List<String> {
            return ALL_LABELS.filter { donor -> isCompatibleDonor(donor, recipientGroup) }
        }
    }
}
