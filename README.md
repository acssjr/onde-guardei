# Onde Guardei? 📦

Um aplicativo Android para ajudar você a lembrar onde guardou suas coisas!

## Sobre o App

"Onde Guardei?" é um aplicativo simples e intuitivo que permite registrar itens e seus locais de armazenamento, com fotos para facilitar a identificação.

## Funcionalidades

- ✅ **Registrar itens** com nome, local e descrição
- 📸 **Tirar fotos** dos itens usando a câmera
- 🖼️ **Escolher fotos** da galeria
- 📋 **Listar todos os itens** cadastrados
- 🔍 **Ver detalhes** completos de cada item
- ✏️ **Editar** informações dos itens
- 🗑️ **Excluir** itens que não precisa mais

## Tecnologias Utilizadas

- **Kotlin** - Linguagem de programação
- **Jetpack Compose** - Interface moderna e declarativa
- **Room Database** - Persistência de dados local
- **CameraX** - Captura de fotos
- **Coil** - Carregamento de imagens
- **Navigation Compose** - Navegação entre telas
- **Material Design 3** - Design moderno e responsivo

## Arquitetura

O app segue as melhores práticas de desenvolvimento Android:

- **MVVM** (Model-View-ViewModel)
- **Repository Pattern**
- **Flows** para dados reativos
- **Coroutines** para operações assíncronas

## Estrutura do Projeto

```
app/src/main/java/com/example/ondeguardei/
├── data/              # Camada de dados
│   ├── Item.kt        # Entidade do banco de dados
│   ├── ItemDao.kt     # Data Access Object
│   ├── AppDatabase.kt # Configuração do Room
│   └── ItemRepository.kt
├── ui/                # Telas do app
│   ├── ItemListScreen.kt      # Lista de itens
│   ├── AddEditItemScreen.kt   # Adicionar/editar item
│   ├── ItemDetailScreen.kt    # Detalhes do item
│   ├── CameraScreen.kt        # Tela da câmera
│   └── theme/         # Tema e cores
├── viewmodel/         # ViewModels
│   └── ItemViewModel.kt
└── MainActivity.kt    # Activity principal
```

## Como Usar

1. **Adicionar um item:**
   - Toque no botão ➕ na tela principal
   - Preencha o nome do item e onde está guardado
   - Opcionalmente, adicione uma descrição e foto
   - Toque em "Salvar"

2. **Tirar foto:**
   - Na tela de adicionar/editar, toque na área da foto
   - Escolha "Tirar foto" ou "Escolher da galeria"
   - Se escolher câmera, tire a foto e ela será automaticamente adicionada

3. **Ver detalhes:**
   - Toque em qualquer item da lista
   - Veja todas as informações e a foto em tamanho maior

4. **Editar:**
   - Na tela de detalhes, toque no ícone de edição ✏️
   - Modifique as informações desejadas
   - Toque em "Salvar"

5. **Excluir:**
   - Na lista, toque no ícone de lixeira 🗑️
   - Ou na tela de detalhes, toque no ícone de lixeira
   - Confirme a exclusão

## Requisitos

- Android 7.0 (API 24) ou superior
- Permissão de câmera (solicitada automaticamente)
- Permissão de leitura de imagens (para escolher da galeria)

## Build

Para compilar o projeto:

```bash
./gradlew assembleDebug
```

Para instalar no dispositivo conectado:

```bash
./gradlew installDebug
```

## Licença

Este projeto é de código aberto e está disponível para uso livre.

## Autor

Desenvolvido com ❤️ para ajudar pessoas a encontrarem suas coisas!
