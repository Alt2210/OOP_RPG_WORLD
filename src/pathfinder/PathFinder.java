package pathfinder;

import character.Character; // Sửa lại import cho đúng package character của bạn
import main.GamePanel;
// import tile_interactive.InteractiveTile; // Bỏ comment và sửa nếu cần

import java.util.ArrayList;

public class PathFinder {

    private GamePanel gp;
    private Node[][] node;
    private ArrayList<Node> openList = new ArrayList<>();
    private ArrayList<Node> pathList = new ArrayList<>();
    private Node startNode, goalNode, currentNode;
    private boolean goalReached = false;
    private int step = 0;
    final int MAX_SEARCH_STEPS = 1000; // Giới hạn số bước tìm kiếm

    public ArrayList<Node> getPathList() {
        return pathList;
    }

    public void setPathList(ArrayList<Node> pathList) {
        this.pathList = pathList;
    }

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
                    node[col][row].setOpen(false);
                    node[col][row].setChecked(false);
                    node[col][row].setSolid(false);
                    node[col][row].setParent(null);
                    node[col][row].setgCost(Integer.MAX_VALUE); // Khởi tạo gCost lớn
                    node[col][row].sethCost(0);
                    node[col][row].setfCost(Integer.MAX_VALUE); // Khởi tạo fCost lớn
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

        startNode.setgCost(0);
        calculateHCost(startNode);
        startNode.setfCost(startNode.getgCost() + startNode.gethCost());
        startNode.setOpen(true);
        openList.add(startNode);

        // Đánh dấu các node solid TỪ MAP HIỆN TẠI
        for (int r = 0; r < gp.getMaxWorldRow(); r++) {
            for (int c = 0; c < gp.getMaxWorldCol(); c++) {
                if (node[c][r] == null) continue;

                // Từ Tiles - SỬ DỤNG gp.getCurrentMp(
                // Đảm bảo gp.getCurrentMp( là hợp lệ (0 <= gp.getCurrentMp( < gp.getMaxMap()
                if (gp.getCurrentMapIndex() < 0 || gp.getCurrentMapIndex() >= gp.getMaxMap()) {
                    System.err.println("PathFinder Error: gp.getCurrentMapIndex() (" + gp.getCurrentMapIndex() + ") không hợp lệ!");
                    return;
                }
                // Đảm bảo TileManager và mapTileNum đã được khởi tạo đúng
                if (gp.getTileM() == null || gp.getTileM().getMapTileNum() == null ||
                        gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()] == null) {
                    System.err.println("PathFinder Error: TileManager hoặc mapTileNum cho map hiện tại chưa được khởi tạo!");
                    return;
                }

                // Kiểm tra biên cho truy cập mapTileNum[gp.getCurrentMp(][c][r]
                if (c < 0 || c >= gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()].length ||
                        r < 0 || r >= gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][c].length) {
                    System.err.println("PathFinder Warning: Truy cập ngoài biên mapTileNum cho map " + gp.getCurrentMapIndex() + " tại (" + c + "," + r + "). Bỏ qua tile này.");
                    continue;
                }


                int tileNum = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][c][r];
                // Giả sử mapTileNum là [mapIndex][col][row]

                if (tileNum >= 0 && tileNum < gp.getTileM().getTile().length &&
                        gp.getTileM().getTile()[tileNum] != null &&
                        gp.getTileM().getTile()[tileNum].isCollision()) {
                    node[c][r].setSolid(true);
                }

                // Từ InteractiveTiles (Nếu bạn có hệ thống này, nó cũng cần nhận biết currentMap)
                /*
                if (gp.iTile != null && gp.iTile[gp.getCurrentMp(] != null) { // Giả sử iTile là mảng 2 chiều [mapIndex][tileIndex]
                    for (InteractiveTile iTile : gp.iTile[gp.getCurrentMp(]) {
                        if (iTile != null && iTile.isObstacle()) {
                            int itCol = iTile.worldX / gp.getTileSize();
                            int itRow = iTile.worldY / gp.getTileSize();
                            if (itCol == c && itRow == r) {
                                node[c][r].isSolid() = true;
                                break;
                            }
                        }
                    }
                }
                */
            }
        }
        // Đảm bảo startNode và goalNode không phải là solid sau khi thiết lập từ tiles/itiles
        if (startNode.isSolid()) {
            // System.err.println("PathFinder Info: Start node is solid after map setup. Path might fail.");
            // Bạn có thể quyết định startNode.isSolid() = false; nếu entity có thể đứng trên đó,
            // hoặc để nguyên và thuật toán sẽ không tìm được đường.
        }
        if (goalNode.isSolid()) {
            // System.err.println("PathFinder Info: Goal node is solid after map setup. Path might fail.");
        }
    }
    private void calculateHCost(Node nodeToCalc) {
        if (nodeToCalc == null || goalNode == null) return;
        int xDistance = Math.abs(nodeToCalc.getCol() - goalNode.getCol());
        int yDistance = Math.abs(nodeToCalc.getRow() - goalNode.getRow());
        nodeToCalc.sethCost(xDistance + yDistance); // Manhattan distance
    }


    public boolean search() {
//        if (startNode == null || goalNode == null) return false; // Đã kiểm tra trong setNodes
       if (startNode.isSolid() || goalNode.isSolid()) return false; // Không tìm đường nếu start/goal là vật cản

        while (!goalReached && step < MAX_SEARCH_STEPS) {
            if (openList.isEmpty()) {
                // Không còn node nào để xét
                return false;
            }

            // Tìm node có fCost nhỏ nhất trong openList
            int bestNodeIndex = 0;
            for (int i = 1; i < openList.size(); i++) {
                if (openList.get(i).getfCost() < openList.get(bestNodeIndex).getfCost()) {
                    bestNodeIndex = i;
                } else if (openList.get(i).getfCost() == openList.get(bestNodeIndex).getfCost()) {
                    // Nếu fCost bằng nhau, ưu tiên node có hCost nhỏ hơn (gần đích hơn)
                    if (openList.get(i).gethCost() < openList.get(bestNodeIndex).gethCost()) {
                        bestNodeIndex = i;
                    }
                }
            }
            currentNode = openList.get(bestNodeIndex);

            // Di chuyển currentNode từ openList sang closedList (đánh dấu checked)
            openList.remove(bestNodeIndex);
            currentNode.setOpen(true); // Không còn trong open list nữa
            currentNode.setChecked(true);

            // Nếu currentNode là goalNode, đã tìm thấy đường
            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
                return true;
            }

            // Mở các node kề
            openAdjacentNode(currentNode.getCol(), currentNode.getRow() - 1); // UP
            openAdjacentNode(currentNode.getCol(), currentNode.getRow() + 1); // DOWN
            openAdjacentNode(currentNode.getCol() - 1, currentNode.getRow()); // LEFT
            openAdjacentNode(currentNode.getCol() + 1, currentNode.getRow()); // RIGHT

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

        if (adjacentNode == null || adjacentNode.isSolid() || adjacentNode.isChecked()) {
            return; // Bỏ qua nếu node không hợp lệ, là vật cản, hoặc đã được xét
        }

        // Chi phí di chuyển từ currentNode đến adjacentNode (thường là 1 cho ô kề, 1.414 cho ô chéo)
        int movementCost = 1; // (Nếu có di chuyển chéo, bạn cần tính toán lại)

        int newGCost = currentNode.getgCost() + movementCost;

        if (!adjacentNode.isOpen()) { // Nếu node chưa trong openList
            adjacentNode.setParent(currentNode);
            adjacentNode.setgCost(newGCost);
            calculateHCost(adjacentNode); // Tính H cost
            adjacentNode.setfCost(adjacentNode.getgCost() + adjacentNode.gethCost());
            adjacentNode.setOpen(true);
            openList.add(adjacentNode);
        } else if (newGCost < adjacentNode.getgCost()) { // Nếu node đã trong openList nhưng tìm thấy đường tốt hơn
            adjacentNode.setParent(currentNode);
            adjacentNode.setgCost(newGCost);
            adjacentNode.setfCost(adjacentNode.getgCost() + adjacentNode.gethCost()); // Tính lại fCost
        }
    }

    public void trackThePath() {
        Node current = goalNode;
        pathList.clear();

        while (current != startNode && current != null && current.getParent() != null) {
            pathList.add(0, current);
            current = current.getParent();
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