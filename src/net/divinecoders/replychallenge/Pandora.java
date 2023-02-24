package net.divinecoders.replychallenge;

public class Pandora
{
    private int stamina;
    private boolean alreadyFought = false;
    private final int maxStamina;

    public Pandora(int beginningStamina, int maxStamina)
    {
        stamina = beginningStamina;
        this.maxStamina = maxStamina;
    }

    public int getStamina()
    { return stamina; }

    public void addStamina(int amount)
    {
        this.stamina += amount;
        if(this.stamina > maxStamina)
        {
            this.stamina = maxStamina;
        }
    }

    public void fight(Demon demon)
    {
        if(stamina >= demon.staminaLoss && !alreadyFought)
        {
            System.out.println("Pandora will now fight a demon. " + demon.getInfo());

            stamina -= demon.staminaLoss;
            alreadyFought = true;

            Main.staminaRecoveryTurns.add(demon.recoverTurns);
            Main.staminaRecoveryAmount.add(demon.recoveredStamina);

            Main.demons.remove(demon);
        }
    }

    public void nextTurn()
    {
        alreadyFought = false;
    }

}
