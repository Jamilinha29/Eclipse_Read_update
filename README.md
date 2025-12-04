# Eclipse Reads

Eclipse Reads é um aplicativo de leitura para Android que permite aos usuários descobrir, rastrear e avaliar livros. O aplicativo foi construído com foco em uma arquitetura moderna, robusta e escalável, garantindo uma experiência de usuário fluida, mesmo em condições de rede instáveis.

## Arquitetura

O aplicativo segue as diretrizes de arquitetura recomendadas pelo Google, utilizando os seguintes componentes principais:

*   **MVVM (Model-View-ViewModel):** Separa a lógica de negócios da interface do usuário, promovendo um código mais limpo e testável.
*   **Injeção de Dependência (Hilt):** Gerencia as dependências do projeto, facilitando a modularidade e os testes.
*   **Kotlin Coroutines & Flow:** Para um gerenciamento de concorrência moderno e eficiente.
*   **Camada de Dados com Padrão Repositório:** Abstrai as fontes de dados (rede e local) do resto do aplicativo.
    *   **Fonte de Dados Remota (Rede):** Utiliza o **Supabase** para todas as operações de backend (autenticação, banco de dados, armazenamento).
    *   **Fonte de Dados Local (Cache):** Utiliza o **Room** como um banco de dados local para habilitar o modo offline e servir como uma "fonte da verdade" para a UI.

## Funcionalidades Principais

*   **Modo Offline Completo:** O aplicativo continua funcionando sem conexão com a internet. As alterações são salvas localmente e sincronizadas quando a conexão é restabelecida.
*   **Sincronização em Segundo Plano:** Utiliza o **WorkManager** para sincronizar os dados locais com o servidor periodicamente, de forma eficiente em termos de bateria (apenas com rede e bateria não baixa).
*   **Paginação de Dados:** A lista de livros usa a biblioteca **Paging 3** para carregar dados de forma incremental, garantindo uma rolagem suave e baixo consumo de memória.
*   **Segurança:**
    *   As sessões de usuário são armazenadas de forma segura usando **EncryptedSharedPreferences** e o Android Keystore.
    *   O tráfego de rede é protegido com uma **Network Security Config** que força o uso de HTTPS.
*   **Detecção de Vazamento de Memória:** O **LeakCanary** está integrado para detectar e ajudar a resolver vazamentos de memória em builds de depuração.
*   **Relatórios de Falhas:** O **Sentry** está integrado para capturar e relatar falhas em tempo real em builds de produção.

## Como Construir

1.  **Clone o repositório:**
    ```sh
    git clone <URL_DO_REPOSITORIO>
    ```
2.  **Configure o Supabase:**
    *   Adicione suas credenciais do Supabase (URL e Chave Anônima) ao arquivo `app/build.gradle.kts` nos campos apropriados.
3.  **Configure o Sentry:**
    *   Adicione o seu DSN (Data Source Name) do Sentry ao arquivo `app/src/main/AndroidManifest.xml`.
4.  **Construa o projeto:**
    *   Abra o projeto no Android Studio e sincronize os arquivos Gradle. Em seguida, você pode construir e executar o aplicativo em um emulador ou dispositivo.
