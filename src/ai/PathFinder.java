package ai;

import character.Character; // Sửa lại import cho đúng package character của bạn
import main.GamePanel;
// import tile_interactive.InteractiveTile; // Bỏ comment và sửa nếu cần

import java.util.ArrayList;

public class PathFinder {

    GamePanel gp;
    Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;
    final int MAX_SEARCH_STEPS = 1000; // Giới hạn số bước tìm kiếm

    public PathFinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
    }

    public void instantiateNodes() {
        node = new Node[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
        for (int row = 0; row < gp.getMaxWorldRow(); row++) {
            for (int col = 0; col < gp.getMaxWorldCol(); col++) {
                node[col][row] = new Node(col, row);
            }
        }
    }

    public void resetNodes() {
        for (int row = 0; row < gp.getMaxWorldRow(); row++) {
            for (int col = 0; col < gp.getMaxWorldCol(); col++) {
                if (node[col][row] != null) {
                    node[col][row].open = false;
                    node[col][row].checked = false;
                    node[col][row].solid = false;
                    node[col][row].parent = null;
                    node[col][row].gCost = Integer.MAX_VALUE; // Khởi tạo gCost lớn
                    node[col][row].hCost = 0;
                    node[col][row].fCost = Integer.MAX_VALUE; // Khởi tạo fCost lớn
                }
            }
        }
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;
    }

    public void setNodes(int startCol, int startRow, int goalCol, int goalRow, Character entity) {
        resetNodes();

        if (startCol < 0 || startCol >= gp.getMaxWorldCol() || startRow < 0 || startRow >= gp.getMaxWorldRow() ||
                goalCol < 0 || goalCol >= gp.getMaxWorldCol() || goalRow < 0 || goalRow >= gp.getMaxWorldRow() ||
                node[startCol][startRow] == null || node[goalCol][goalRow] == null) {
            System.err.println("PathFinder Error: Start or Goal node is out of bounds or null.");
            goalReached = false;
            return;
        }

        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];

        startNode.gCost = 0;
        calculateHCost(startNode);
        startNode.fCost = startNode.gCost + startNode.hCost;
        startNode.open = true;
        openList.add(startNode);

        // Đánh dấu các node solid TỪ MAP HIỆN TẠI
        for (int r = 0; r < gp.getMaxWorldRow(); r++) {
            for (int c = 0; c < gp.getMaxWorldCol(); c++) {
                if (node[c][r] == null) continue;

                // Từ Tiles - SỬ DỤNG gp.currentMap
                // Đảm bảo gp.currentMap là hợp lệ (0 <= gp.currentMap < gp.maxMap)
                if (gp.currentMap < 0 || gp.currentMap >= gp.maxMap) {
                    System.err.println("PathFinder Error: gp.currentMap (" + gp.currentMap + ") không hợp lệ!");
                    return;
                }
                // Đảm bảo TileManager và mapTileNum đã được khởi tạo đúng
                if (gp.getTileM() == null || gp.getTileM().mapTileNum == null ||
                        gp.getTileM().mapTileNum[gp.currentMap] == null) {
                    System.err.println("PathFinder Error: TileManager hoặc mapTileNum cho map hiện tại chưa được khởi tạo!");
                    return;
                }

                // Kiểm tra biên cho truy cập mapTileNum[gp.currentMap][c][r]
                if (c < 0 || c >= gp.getTileM().mapTileNum[gp.currentMap].length ||
                        r < 0 || r >= gp.getTileM().mapTileNum[gp.currentMap][c].length) {
                    System.err.println("PathFinder Warning: Truy cập ngoài biên mapTileNum cho map " + gp.currentMap + " tại (" + c + "," + r + "). Bỏ qua tile này.");
                    continue;
                }


                int tileNum = gp.getTileM().mapTileNum[gp.currentMap][c][r];
                // Giả sử mapTileNum là [mapIndex][col][row]

                if (tileNum >= 0 && tileNum < gp.getTileM().tile.length &&
                        gp.getTileM().tile[tileNum] != null &&
                        gp.getTileM().tile[tileNum].collision) {
                    node[c][r].solid = true;
                }

                // Từ InteractiveTiles (Nếu bạn có hệ thống này, nó cũng cần nhận biết currentMap)
                /*
                if (gp.iTile != null && gp.iTile[gp.currentMap] != null) { // Giả sử iTile là mảng 2 chiều [mapIndex][tileIndex]
                    for (InteractiveTile iTile : gp.iTile[gp.currentMap]) {
                        if (iTile != null && iTile.isObstacle()) {
                            int itCol = iTile.worldX / gp.getTileSize();
                            int itRow = iTile.worldY / gp.getTileSize();
                            if (itCol == c && itRow == r) {
                                node[c][r].solid = true;
                                break;
                            }
                        }
                    }
                }
                */
            }
        }
        // Đảm bảo startNode và goalNode không phải là solid sau khi thiết lập từ tiles/itiles
        if (startNode.solid) {
            // System.err.println("PathFinder Info: Start node is solid after map setup. Path might fail.");
            // Bạn có thể quyết định startNode.solid = false; nếu entity có thể đứng trên đó,
            // hoặc để nguyên và thuật toán sẽ không tìm được đường.
        }
        if (goalNode.solid) {
            // System.err.println("PathFinder Info: Goal node is solid after map setup. Path might fail.");
        }
    }
    private void calculateHCost(Node nodeToCalc) {
        if (nodeToCalc == null || goalNode == null) return;
        int xDistance = Math.abs(nodeToCalc.col - goalNode.col);
        int yDistance = Math.abs(nodeToCalc.row - goalNode.row);
        nodeToCalc.hCost = xDistance + yDistance; // Manhattan distance
    }


    public boolean search() {
//        if (startNode == null || goalNode == null) return false; // Đã kiểm tra trong setNodes
       if (startNode.solid || goalNode.solid) return false; // Không tìm đường nếu start/goal là vật cản

        while (!goalReached && step < MAX_SEARCH_STEPS) {
            if (openList.isEmpty()) {
                // Không còn node nào để xét
                return false;
            }

            // Tìm node có fCost nhỏ nhất trong openList
            int bestNodeIndex = 0;
            for (int i = 1; i < openList.size(); i++) {
                if (openList.get(i).fCost < openList.get(bestNodeIndex).fCost) {
                    bestNodeIndex = i;
                } else if (openList.get(i).fCost == openList.get(bestNodeIndex).fCost) {
                    // Nếu fCost bằng nhau, ưu tiên node có hCost nhỏ hơn (gần đích hơn)
                    if (openList.get(i).hCost < openList.get(bestNodeIndex).hCost) {
                        bestNodeIndex = i;
                    }
                }
            }
            currentNode = openList.get(bestNodeIndex);

            // Di chuyển currentNode từ openList sang closedList (đánh dấu checked)
            openList.remove(bestNodeIndex);
            currentNode.open = false; // Không còn trong open list nữa
            currentNode.checked = true;

            // Nếu currentNode là goalNode, đã tìm thấy đường
            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
                return true;
            }

            // Mở các node kề
            openAdjacentNode(currentNode.col, currentNode.row - 1); // UP
            openAdjacentNode(currentNode.col, currentNode.row + 1); // DOWN
            openAdjacentNode(currentNode.col - 1, currentNode.row); // LEFT
            openAdjacentNode(currentNode.col + 1, currentNode.row); // RIGHT

            // Tùy chọn: Mở các node chéo nếu cho phép di chuyển chéo
            // openAdjacentNode(currentNode.col - 1, currentNode.row - 1); // UP-LEFT
            // openAdjacentNode(currentNode.col + 1, currentNode.row - 1); // UP-RIGHT
            // openAdjacentNode(currentNode.col - 1, currentNode.row + 1); // DOWN-LEFT
            // openAdjacentNode(currentNode.col + 1, currentNode.row + 1); // DOWN-RIGHT

            step++;
        }
        return false; // Không tìm thấy đường đi sau số bước giới hạn hoặc openList rỗng
    }

    private void openAdjacentNode(int col, int row) {
        // Kiểm tra biên
        if (col < 0 || col >= gp.getMaxWorldCol() || row < 0 || row >= gp.getMaxWorldRow()) {
            return;
        }

        Node adjacentNode = node[col][row];

        if (adjacentNode == null || adjacentNode.solid || adjacentNode.checked) {
            return; // Bỏ qua nếu node không hợp lệ, là vật cản, hoặc đã được xét
        }

        // Chi phí di chuyển từ currentNode đến adjacentNode (thường là 1 cho ô kề, 1.414 cho ô chéo)
        int movementCost = 1; // (Nếu có di chuyển chéo, bạn cần tính toán lại)

        int newGCost = currentNode.gCost + movementCost;

        if (!adjacentNode.open) { // Nếu node chưa trong openList
            adjacentNode.parent = currentNode;
            adjacentNode.gCost = newGCost;
            calculateHCost(adjacentNode); // Tính H cost
            adjacentNode.fCost = adjacentNode.gCost + adjacentNode.hCost;
            adjacentNode.open = true;
            openList.add(adjacentNode);
        } else if (newGCost < adjacentNode.gCost) { // Nếu node đã trong openList nhưng tìm thấy đường tốt hơn
            adjacentNode.parent = currentNode;
            adjacentNode.gCost = newGCost;
            adjacentNode.fCost = adjacentNode.gCost + adjacentNode.hCost; // Tính lại fCost
        }
    }

    public void trackThePath() {
        Node current = goalNode;
        pathList.clear();

        while (current != startNode && current != null && current.parent != null) {
            pathList.add(0, current);
            current = current.parent;
            if (pathList.size() > gp.getMaxWorldCol() * gp.getMaxWorldRow()) {
                System.err.println("PathFinder Error: Path tracking loop exceeded max size.");
                pathList.clear();
                break;
            }
        }
        // Nếu bạn muốn bao gồm cả startNode trong pathList khi tìm thấy đường
        // if (goalReached && startNode != null) {
        //     pathList.add(0, startNode);
        // }
    }
}