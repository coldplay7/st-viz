graph TD
    subgraph "Shared Code (commonMain)"
        A[<b>UI</b><br/>Compose UI] --> B[<b>Logic</b><br/>ViewModels]
        B --> C[<b>Storage</b><br/>JWT/Settings]
        B --> D[<b>Network</b><br/>Ktor API Client]
    end

    A --> E[<b>Android App</b>]
    A --> F[<b>iOS App</b>]
    A --> G[<b>Web Browser</b><br/>(Wasm/JS)]
    
    style A fill:#3DDC84,stroke:#333,stroke-width:2px,color:#fff
    style G fill:#F0DB4F,stroke:#333,stroke-width:2px,color:#000
    style F fill:#000,stroke:#333,stroke-width:2px,color:#fff
