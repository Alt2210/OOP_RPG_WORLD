package ai;

import entity.Character; // Đảm bảo import đúng lớp Character
import main.GamePanel;
import tile_interactive.InteractiveTile; // Giả sử bạn có lớp này và nó có các thuộc tính cần thiết

import java.util.ArrayList;

public class PathFinder {

    GamePanel gp;
    Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>(); // Danh sách các node trên đường đi
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;

    public PathFinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
    }

    public void instantiateNodes() {
        node = new Node[gp.maxWorldCol][gp.maxWorldRow];
        int col = 0;
        int row = 0;
        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
            node[col][row] = new Node(col, row);
            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }
    }

    public void resetNodes() {
        int col = 0;
        int row = 0;
        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
            node[col][row].open = false;
            node[col][row].checked = false;
            node[col][row].solid = false;
            node[col][row].parent = null; // Reset cả parent để tránh lỗi path cũ
            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;
    }

    public void setNodes(int startCol, int startRow, int goalCol, int goalRow, Character entity) {
        resetNodes();

        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];
        openList.add(currentNode);

        int col = 0;
        int row = 0;
        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
            // SET SOLID NODE FROM TILES
            // Giả sử gp.tileM.mapTileNum và gp.tileM.tile đã được khởi tạo đúng
            if (gp.tileM.mapTileNum[gp.currentMap][col][row] >= 0 &&
                    gp.tileM.mapTileNum[gp.currentMap][col][row] < gp.tileM.tile.length && // Kiểm tra biên cho tileNum
                    gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][col][row]] != null &&
                    gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][col][row]].collision) {
                node[col][row].solid = true;
            }

            // SET SOLID NODE FROM INTERACTIVE TILES
            // Giả sử gp.iTile là InteractiveTile[maxMap][số lượng]
            // và InteractiveTile có worldX, worldY, destructible
            if (gp.iTile != null && gp.iTile[gp.currentMap] != null) {
                for (int i = 0; i < gp.iTile[gp.currentMap].length; i++) {
                    InteractiveTile iTile = gp.iTile[gp.currentMap][i];
                    if (iTile != null && iTile.destructible) { // Chỉ coi iTile có thể phá hủy là vật cản
                        // Nếu InteractiveTile kế thừa từ GameObject, nó sẽ có worldX, worldY
                        // Hoặc nếu không, bạn cần đảm bảo nó có các trường này
                        int itCol = iTile.worldX / gp.tileSize;
                        int itRow = iTile.worldY / gp.tileSize;
                        // Kiểm tra xem itCol và itRow có trùng với col, row hiện tại không
                        if (itCol == col && itRow == row) {
                            node[col][row].solid = true; // Đánh dấu node này là solid
                        }
                    }
                }
            }


            // SET COST
            getCost(node[col][row]);

            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }
    }

    public void getCost(Node node) {
        // G Cost: Distance from start node
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;

        // H Cost: Distance from goal node (Heuristic - Manhattan distance)
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;

        // F Cost: Total cost
        node.fCost = node.gCost + node.hCost;
    }

    public boolean search() {
        while (!goalReached && step < 500) { // Giới hạn 500 bước để tránh vòng lặp vô hạn
            int col = currentNode.col;
            int row = currentNode.row;

            currentNode.checked = true;
            openList.remove(currentNode);

            // Open adjacent nodes
            // UP
            if (row - 1 >= 0) {
                openNode(node[col][row - 1]);
            }
            // LEFT
            if (col - 1 >= 0) {
                openNode(node[col - 1][row]);
            }
            // DOWN
            if (row + 1 < gp.maxWorldRow) { // Sửa: <= thành <
                openNode(node[col][row + 1]);
            }
            // RIGHT
            if (col + 1 < gp.maxWorldCol) { // Sửa: <= thành <
                openNode(node[col + 1][row]);
            }

            // Find the best node in the openList
            int bestNodeIndex = -1;
            int bestNodefCost = Integer.MAX_VALUE;

            for (int i = 0; i < openList.size(); i++) {
                if (openList.get(i).fCost < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                }
                // If F cost is equal, check the G cost (prefer shorter path from start)
                else if (openList.get(i).fCost == bestNodefCost) {
                    if (bestNodeIndex == -1 || openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }

            // If there is no node in the openList or no valid best node, end the loop
            if (openList.isEmpty() || bestNodeIndex == -1) {
                break;
            }

            currentNode = openList.get(bestNodeIndex);

            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
            step++;
        }
        return goalReached;
    }

    public void openNode(Node nodeToOpen) {
        if (!nodeToOpen.open && !nodeToOpen.checked && !nodeToOpen.solid) {
            nodeToOpen.open = true;
            nodeToOpen.parent = currentNode; // Set parent for path tracking
            openList.add(nodeToOpen);
        }
    }

    public void trackThePath() {
        Node current = goalNode;
        pathList.clear(); // Clear previous path before adding new one

        while (current != startNode && current != null) {
            pathList.add(0, current); // Add to the beginning of the list to reverse the path
            current = current.parent;

            // Safety break to prevent infinite loop in case of logical error in parent assignment
            if (pathList.size() > gp.maxWorldCol * gp.maxWorldRow) {
                System.err.println("PathFinder Error: Path tracking exceeded maximum possible length. Breaking loop.");
                pathList.clear(); // Clear the potentially corrupt path
                break;
            }
        }
        // Add the start node if a path was found (optional, depends on how you use pathList)
        // if (!pathList.isEmpty() || goalNode == startNode) { // if goal is start, path is just start
        //     pathList.add(0, startNode);
        // }
    }
}