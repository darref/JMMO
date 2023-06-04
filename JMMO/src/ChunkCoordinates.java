public class ChunkCoordinates {
    public int x;
    public int y;

    public ChunkCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Ajoutez des méthodes getter pour les coordonnées x et y
    // ...
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChunkCoordinates other = (ChunkCoordinates) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}