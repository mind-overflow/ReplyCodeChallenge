package net.divinecoders.replychallenge;

public class Demon
{
    final int staminaLoss;
    final int recoverTurns;
    final int recoveredStamina;
    final int fragmentTurns;
    final int fragments;

    public Demon(int staminaLoss,
                 int recoverTurns,
                 int recoveredStamina,
                 int fragmentTurns,
                 int fragments)
    {
        this.staminaLoss = staminaLoss;
        this.recoverTurns = recoverTurns;
        this.recoveredStamina = recoveredStamina;
        this.fragmentTurns = fragmentTurns;
        this.fragments = fragments;
    }

    public String getInfo()
    {
        return "This demon needs " + staminaLoss + " stamina. She will recover " + recoveredStamina + " in " + recoverTurns + " turns.";
    }
}
