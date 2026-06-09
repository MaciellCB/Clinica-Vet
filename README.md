# 🏥 Clínica Veterinária - Sistema de Gestão

Sistema desktop em Java para cadastro de clientes e seus animais (cachorros e gatos), incluindo cadastro de raças, permitindo operações de CRUD e consultas.

## 📋 Funcionalidades

-  Cadastrar, alterar e inativar Clientes (Exclusão Lógica)
-  Cadastrar, alterar e inativar Raças de Animais
-  Cadastrar, alterar e inativar Animais vinculados a um cliente e raça
-  Consultar Animais por Cliente (Pesquisa com filtros por Nome ou CPF)
-  Geração de Relatórios em formato `.txt`:
  - Todos os clientes e seus animais
  - Animais aniversariantes de um mês específico
  - Clientes aniversariantes de um mês específico

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java
- **Interface:** Desktop / Swing
- **Banco de Dados:** MySQL utilizando JDBC para conexão e persistência
- **Arquitetura:** Orientação a Objetos (POO) com organização em pacotes (`factory`, `modelo`, `dao`, `gui`)

## 📁 Estrutura do Projeto

```text
/
├── diagramas/                   # Imagens dos diagramas do banco e UML
├── lib/                         # Bibliotecas externas (MySQL JDBC)
├── src/   
│   ├── dao/                     # Data Access Objects para persistência
│   ├── factory/                 # Factory para conexão com MySQL
│   ├── gui/                     # Interfaces gráficas e Main.java
│   └── modelo/                  # Classes de modelo (entidades)
├── telas/                       # Prints das telas para o manual
├── README.md                    # Documentação do projeto
├── relatorio.txt                # Exemplo de saída gerada pelo sistema
└── Script.sql                   # Script de criação do banco de dados