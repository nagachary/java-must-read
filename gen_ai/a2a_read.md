1) Wrap your agent in A2A agent and Publish the agent card.
2) Agent card is a JSON document which publishes
    1> Capabilities
    2> Endpoints
    3> Authentication Requirement
3) Agent client itself is an Agent, discovers the agent structure the request using the agent card.
4) it is a seamless-standardized communication

       |--------------------| 
       |                    |
    i) | Orchestrator Agent |==> A2A Client => Logistic Agent (A2A Server)
       |____________________|      
             |
             |=================> A2A Client => Product RAG Agent (A2A Server)
5) MCP connects agent to external system
6) A2A is a standardized seamless connection to a peer agent
