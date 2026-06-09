CREATE DATABASE IF NOT EXISTS clinica_veterinaria;
USE clinica_veterinaria;

CREATE TABLE raca (
    id_raca INT AUTO_INCREMENT PRIMARY KEY,
    nome_raca VARCHAR(100) NOT NULL,
    tipo_animal VARCHAR(20) NOT NULL,
    status BOOLEAN DEFAULT TRUE
);

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    data_nascimento DATE,
    telefone VARCHAR(20),
    endereco VARCHAR(150),
    bairro VARCHAR(50),
    cidade VARCHAR(50),
    estado CHAR(2),
    cep VARCHAR(10),
    status BOOLEAN DEFAULT TRUE
);

CREATE TABLE animal (
    id_animal INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE,
    sexo CHAR(1),
    cor VARCHAR(50),
    observacoes TEXT,
    id_cliente INT NOT NULL,
    id_raca INT NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_raca) REFERENCES raca(id_raca)
);

-- Inserções iniciais para facilitar os testes da professora
INSERT INTO raca (nome_raca, tipo_animal) VALUES 
('Vira-lata (SRD)', 'Cachorro'), 
('Siamês', 'Gato');