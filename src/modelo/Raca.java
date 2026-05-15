package modelo;

public class Raca {
    private int idRaca;
    private String nomeRaca;
    private String tipoAnimal; // Cachorro ou Gato
    private boolean status = true;

    public Raca() {}

    public int getIdRaca() { return idRaca; }
    public void setIdRaca(int idRaca) { this.idRaca = idRaca; }
    public String getNomeRaca() { return nomeRaca; }
    public void setNomeRaca(String nomeRaca) { this.nomeRaca = nomeRaca; }
    public String getTipoAnimal() { return tipoAnimal; }
    public void setTipoAnimal(String tipoAnimal) { this.tipoAnimal = tipoAnimal; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}