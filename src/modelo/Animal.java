package modelo;
import java.time.LocalDate;
import java.time.Period;

public class Animal {
    private int idAnimal;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String cor;
    private String observacoes;
    private int idCliente;
    private int idRaca;
    private boolean status = true;
    private String nomeCliente;
    private String nomeRaca;

    public Animal() {}

    public int getIdade() {
        if (dataNascimento == null) return 0;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public int getIdAnimal() { return idAnimal; }
    public void setIdAnimal(int idAnimal) { this.idAnimal = idAnimal; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdRaca() { return idRaca; }
    public void setIdRaca(int idRaca) { this.idRaca = idRaca; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public String getNomeRaca() { return nomeRaca; }
    public void setNomeRaca(String nomeRaca) { this.nomeRaca = nomeRaca; }
}