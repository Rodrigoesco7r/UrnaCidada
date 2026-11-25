# Urna Cidadã — Simulador Educativo de Voto Eletrônico

Projeto de Extensão Curricular vinculado à disciplina de **Programação Orientada a Objetos (POO)** do curso de **Engenharia de Software** do **Centro Universitário Jorge Amado (UniJorge)** — Semestre 2026.2.

## Equipe
- **Samir Cunha Rosendo Leite** (Coordenação e apresentação)
- **Luis Fernando de Farias Ávila** (Interface gráfica Java Swing e UX)
- **Efraim Lucas Gomes dos Santos Moreira** (Implementação Java e integração Firebase Cloud Firestore)
- **Rodrigo Escorêdo Ferreira** (Testes e material educativo)
- **Orientador:** Prof. Anildo Matos

## Arquitetura e Conceitos de POO Aplicados
- **Abstração e Herança:** Classe abstrata `Pessoa` estendida por `Candidato`.
- **Encapsulamento:** Atributos privados com métodos de acesso controlados nos modelos `Candidato` e `Voto`.
- **Polimorfismo:** Interface `PersistenciaVoto` implementada por `FirebaseVotoService`, permitindo alternar transparentemente entre nuvem (Firestore) e modo offline.
- **Interface Gráfica:** Construída em **Java Swing** (`UrnaFrame`), simulando a votação sequencial para dois cargos (Proporcional e Majoritário) com teclas **BRANCO**, **CORRIGE** e **CONFIRMA**, além de apuração ao vivo.

## Como Executar
Requisitos: **JDK 17 ou superior** e **Maven**, instalado na máquina ou integrado à IDE.

1. Abra o projeto Maven no **IntelliJ IDEA** ou **VS Code**.
2. *(Opcional)* Para gravar os votos na nuvem real, baixe a chave privada do seu projeto Firebase (`serviceAccountKey.json`) e coloque na pasta raiz do projeto. Caso o arquivo não esteja presente, o sistema inicia automaticamente em **modo local de demonstração** para que você possa testar e tirar prints imediatamente.
3. Execute a classe `br.edu.unijorge.urnacidada.Main`.

## Verificar a compilação

Na pasta que contém o `pom.xml`, execute:

```sh
mvn clean package
```

Para abrir a interface, execute a classe `Main` pela IDE com as dependências Maven carregadas.

## Credenciais e modo local

- O arquivo `serviceAccountKey.json` é privado e não deve ser enviado ao GitHub. Ele está incluído no `.gitignore`.
- Cada pessoa que utilizar o Firestore deve configurar sua própria credencial local.
- Sem a credencial, o simulador funciona em memória: os votos são perdidos ao encerrar o programa.
- O projeto é um simulador educativo com candidatos ficticios.
