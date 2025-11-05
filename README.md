# ♟️ Xadrez IA: Implementação "Do Zero" em Java

<p align="center">

</p>

## 🌟 Visão Geral do Projeto

Este projeto é uma **implementação completa de um jogo de Xadrez**, construída **do zero** em Java. Ele não utiliza bibliotecas de Xadrez existentes, o que exigiu o desenvolvimento manual de todas as regras, a lógica de movimentos legais e uma **Inteligência Artificial (IA)** robusta baseada em algoritmos clássicos de busca.

O projeto demonstra proficiência em **Algoritmos, Performance e Testabilidade**, sendo um *playground* para experimentação de técnicas avançadas de IA em jogos complexos.

---

## ⚙️ Stack Tecnológica e Ferramentas

| Categoria | Tecnologia/Conceito | Destaque de Habilidade |
| :--- | :--- | :--- |
| **Linguagem** | **Java 21** | Foco em performance e padrões de código. |
| **Estrutura** | **Programação Orientada a Objetos (POO)** | Alto nível de abstração para peças, tabuleiro e regras. |
| **Interface** | **JavaFX 21** | Interface gráfica rica com suporte a temas dinâmicos. |
| **Qualidade** | **JUnit 5** | Testes unitários abrangentes para regras e IA. |
| **Build** | **Gradle** | Gerenciamento de dependências e automação de build. |
| **Habilidade** | **Lógica Avançada** | Implementação de lógica de jogo complexa e otimização. |

---

## 🧠 Destaques da Inteligência Artificial (IA)

O coração do bot é um motor de busca construído para tomar decisões estratégicas, demonstrando um profundo conhecimento de algoritmos de otimização:

* **Minimax Search:** Algoritmo fundamental para tomada de decisão, buscando maximizar o ganho do bot e minimizar o ganho do adversário.
* **Poda Alfa-Beta (Alpha-Beta Pruning):** Otimização crucial do Minimax, reduzindo o espaço de busca para alcançar uma **profundidade maior** e decisões mais eficientes.
* **Piece-Square Tables (PST):** Uso de tabelas de pontuação de posição que adicionam **estratégia** à avaliação, incentivando o desenvolvimento e a centralização das peças.
* **Busca Quiescence (Quiescence Search):** Técnica de refinamento que estende a busca em posições táticas para evitar o "Horizon Effect" (não enxergar perdas ou ganhos imediatos).

---

## 🧩 Funcionalidades e Arquitetura

### 🎨 Temas Customizáveis
A aplicação oferece uma experiência de usuário aprimorada com a possibilidade de escolher entre **4 temas de tabuleiro** diferentes, configuráveis através de uma aba dedicada.

<p align="center">

</p>

### 🧪 Testes de Alta Cobertura
A **testabilidade** e a **robustez** das regras do jogo e da IA são garantidas por testes de unidade:
* **Testes de Regras (JUnit):** Validação de todos os movimentos especiais, capturas, en passant e roque.
* **Testes do Bot (JUnit):** Testes unitários para os métodos de avaliação, garantindo a correção da pontuação (material, estrutura de peões e segurança do rei).

---

## ⏸️ Status do Projeto e Foco Futuro

O projeto está **pausado** para permitir meu aprofundamento em **Spring Boot e Microserviços**. Este movimento visa alinhar meu portfólio diretamente com as demandas e arquiteturas modernas do desenvolvimento *backend*.

No entanto, o projeto possui um *roadmap* claro para aprimoramento futuro:

* **Refatoração para Performance:** Criação de uma arquitetura **Bitboard** para otimizar dramaticamente o desempenho dos cálculos do motor de xadrez.
* **Melhoria da Avaliação:** Aprimoramento dos métodos de avaliação (King Safety, Mobilitiy) para aprimorar a capacidade estratégica do bot.
* **Organização e Design:** Reorganização e limpeza da estrutura do código para escalabilidade e manutenção de longo prazo.
* **Validação de IA:** Criação de testes de *puzzles* para validar a inteligência e a precisão tática do bot.

---

## 🚀 Como Rodar o Projeto

**Pré-requisitos:**
* Java Development Kit (JDK) 21
* Gradle

```bash
# Clone o repositório
git clone [Seu Link do Repositório]
cd xadrez-ia-java

# Compile e rode o projeto via Gradle
./gradlew run