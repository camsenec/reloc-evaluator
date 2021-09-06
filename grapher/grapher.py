import pandas as pd
import plotly.graph_objects as go
import networkx as nx
import itertools
import os

def generate_client_distribution_graph():

    file = open("../app/log/tx_log.csv", "r")

    groups = []
    for line in file:
        line = line.rstrip("\n")
        data = line.split(',')[1:]
        for i in range(len(data)):
            data[i] = data[i].strip(" '[]\"")
            data[i] = int(data[i])
        groups.append(data)

    for i in range(len(groups)):    
        groups[i] = list(itertools.combinations(groups[i] ,2))


    file.close()

    tmp_nodes = set()
    edges = set()
    for group in groups:
        for pair in group:
            edges.add(pair)
            tmp_nodes.add(pair[0]); tmp_nodes.add(pair[1])
    edges = list(edges)

    clients_df = pd.read_csv("../app/result/client.csv", names = ['numberOfGroups', 'pubNum', 'locality', 'numOfCoop', 'method',"application_id", "client_id", "x", "y", "home_id"], skiprows=1)
    nodes = list()
    for i in range(len(clients_df)):
        if clients_df.iloc[i]["client_id"] in tmp_nodes:
            nodes.append((clients_df.iloc[i]["client_id"], {"pos" : [clients_df.iloc[i]["x"], clients_df.iloc[i]["y"]]}))


    G = nx.Graph()
    G.add_nodes_from(nodes)
    G.add_edges_from(edges)

    edge_x = []
    edge_y = []
    for edge in G.edges():
        x0, y0 = G.nodes[edge[0]]['pos']
        x1, y1 = G.nodes[edge[1]]['pos']
        edge_x.append(x0)
        edge_x.append(x1)
        edge_x.append(None)
        edge_y.append(y0)
        edge_y.append(y1)
        edge_y.append(None)

    edge_trace = go.Scatter(
        x=edge_x, y=edge_y,
        line=dict(width=1.0, color='#888'),
        hoverinfo='none',
        mode='lines')

    node_x = []
    node_y = []
    for node in G.nodes():
        x, y = G.nodes[node]['pos']
        node_x.append(x)
        node_y.append(y)

    node_trace = go.Scatter(
        x=node_x, y=node_y,
        mode='markers',
        hoverinfo='text',
        marker=dict(
            showscale=True,
            colorscale='YlGnBu',
            reversescale=False,
            color=[],
            size=15,
            colorbar=dict(
                thickness=15,
                title='Node Connections',
                xanchor='left',
                titleside='right'
            ),
            line_width=2))

    node_adjacencies = []
    node_text = []
    for node, adjacencies in enumerate(G.adjacency()):
        node_adjacencies.append(len(adjacencies[1]))
        node_text.append('# of connections: '+str(len(adjacencies[1])))

    node_trace.marker.color = node_adjacencies
    node_trace.text = node_text

    fig = go.Figure(data=[edge_trace, node_trace],
                layout=go.Layout(
                    title={"text": "Client distribution", "x": 0.5, "y": 0.95},
                    titlefont_size=32,
                    font_size=32,
                    showlegend=False,
                    hovermode='closest',
                    margin=dict(b=20,l=5,r=100,t=120),
                    width = 1000,
                    height = 1000,
                    xaxis=dict(range=[0,25],showgrid=True, zeroline=True, showticklabels=True, title = 'x (km)'),
                    yaxis=dict(range=[0,25],showgrid=True, zeroline=True, showticklabels=True, title = 'y (km)'))
                    )

    print("saving...")
    fig.show()
    fig.write_image("./out/" + "client_distribution" + ".png")



def generate_resource_comsumption_graph(field, capacity):
    if field == "used":
        color_bar_title = 'Memory to be consumed (%)'
    elif field == "cp":
        color_bar_title = 'Predicted CPU usage (%)'
    else:
        print("Field to be visualized must be 'used (Memory to be consumed)' or 'cp (CPU usage)'")
        exit(1)

    servers_df = pd.read_csv("../app/result/" + "server" + ".csv", 
                            names = ['numberOfGroups', 'pubNum', 'locality', 'numOfCoop', 'method','application_id', 'server_id', 'x', 'y', 'capacity', 'used', 'connection', 'cp', 'mpnum', 
                                    'cluster_id','a', 'b', 'd'],
                            index_col = False,
                            skiprows=1)
    clients_df = pd.read_csv("../app/result/" + "client" + ".csv", 
                                names = ['numberOfGroups', 'pubNum', 'locality', 'numOfCoop', 'method',"application_id", "client_id", "x", "y", "home_id"],
                                skiprows=1)

    edges = set()
    for i in range(len(clients_df)):
        home = clients_df.iloc[i]["home_id"]
        edges.add((clients_df.iloc[i]["client_id"], home))
    edges = list(edges)


    node_clients = list()
    node_clients_colors = list()
    for i in range(len(clients_df)):
        node_clients.append((clients_df.iloc[i]["client_id"], {"pos" : [clients_df.iloc[i]["x"], clients_df.iloc[i]["y"]]}))
        node_clients_colors.append(i)

    node_servers = list()
    for i in range(len(servers_df)):
        node_servers.append((servers_df.iloc[i]["server_id"], {"pos" : [servers_df.iloc[i]["x"], servers_df.iloc[i]["y"]]}))

    G = nx.Graph()
    G.add_nodes_from(node_clients)

    node_x = []
    node_y = []
    for node in G.nodes():
        x, y = G.nodes[node]['pos']
        node_x.append(x)
        node_y.append(y)

    node_trace = go.Scatter(
        x=node_x, y=node_y,
        mode='markers',
        hoverinfo='text',
        marker=dict(
            showscale=False,
            colorscale='YlGnBu',
            reversescale=True,
            color=[],
            size=15,
            line_width=2))

    node_trace.marker.color = 'navy'

    title = "RELOC"
    fig = go.Figure(data=[node_trace],
                    layout=go.Layout(
                        title={"text": "Home server assignment", "x": 0.5, "y": 0.95},
                        titlefont_size=32,
                        font_size=32,
                        showlegend=False,
                        hovermode='closest',
                        margin=dict(b=20,l=5,r=100,t=120),
                        width = 1000,
                        height = 1000,
                        xaxis=dict(range=[0,25],showgrid=True, zeroline=True, showticklabels=True, title='x (km)'),
                        yaxis=dict(range=[0,25],showgrid=True, zeroline=True, showticklabels=True, title='y (km)')),
                    )
    G.clear()
    G.add_nodes_from(node_servers)

    node_x = []
    node_y = []
    for node in G.nodes():
        x, y = G.nodes[node]['pos']
        node_x.append(x)
        node_y.append(y)

    node_trace = go.Scatter(
        x=node_x, y=node_y,
        mode='markers+text',
        hoverinfo='text',
        textposition="middle right",
        marker=dict(
            showscale=True,
            colorscale='Reds',
            reversescale=False,
            colorbar=dict(
                thickness=15,
                title=color_bar_title,
                xanchor='left',
                titleside='right',
            ),
            color=[],
            size=15,
            symbol = "square",
            line_width=2))

    node_trace.marker.color = list(servers_df[field]/capacity * 100)

    fig.add_trace(node_trace)

    G.clear()
    G.add_nodes_from(node_clients + node_servers)
    G.add_edges_from(edges)

    edge_x = []
    edge_y = []
    for edge in G.edges():
        x0, y0 = G.nodes[edge[0]]['pos']
        x1, y1 = G.nodes[edge[1]]['pos']
        edge_x.append(x0)
        edge_x.append(x1)
        edge_x.append(None)
        edge_y.append(y0)
        edge_y.append(y1)
        edge_y.append(None)

    edge_trace = go.Scatter(
        x=edge_x, y=edge_y,
        line=dict(width=1.0, color='#888'),
        hoverinfo='none',
        mode='lines')

    fig.add_trace(edge_trace)


    edge_x = []
    edge_y = []
    edge1, edge2 = [((0,12.5), (12.5,25)),((25,12.5),(12.5,0))]
    for i in range(2):
        x0, y0 = edge1[i]
        x1, y1 = edge2[i]
        edge_x.append(x0)
        edge_x.append(x1)
        edge_x.append(None)
        edge_y.append(y0)
        edge_y.append(y1)
        edge_y.append(None)

    edge_trace = go.Scatter(
        x=edge_x, y=edge_y,
        line=dict(width=2.0, color='gray', dash='dash'),
        hoverinfo='none',
        mode='lines'
    )

    fig.add_trace(edge_trace)
    fig.write_image("./out/" + field + ".png")
    fig.show()

if __name__ == "__main__":
    A = int(os.environ.get("EDGE_SERVER_CAPACITY", 640))
    B = int(os.environ.get("EDGE_SERVER_COMPUTATION_CAPACITY", 3200))
    generate_client_distribution_graph()
    generate_resource_comsumption_graph("used", A)
    generate_resource_comsumption_graph("cp", B)