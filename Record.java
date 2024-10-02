import java.nio.ByteBuffer;

public class Record { // 25 bytes for attributes + 7 bytes padding because java memory alignment in 8
                      // bytes
    private int game_date_est; // 4 bytes
    private int team_id_home; // 4 bytes
    private short pts_home; // 2 bytes
    private float fg_pct_home; // 4 bytes
    private float ft_pct_home; // 4 bytes
    private float fg3_pct_home; // 4 bytes
    private byte ast_home; // 1 byte
    private byte reb_home; // 1 byte
    private byte home_team_wins; // 1 byte
    private byte[] padding; // 7 bytes padding for alignment, ensuring record is 32 bytes

    public Record(int game_date_est, int team_id_home, short pts_home, float fg_pct_home, float ft_pct_home,
            float fg3_pct_home, byte ast_home, byte reb_home, byte home_team_wins) {
        this.game_date_est = game_date_est;
        this.team_id_home = team_id_home;
        this.pts_home = pts_home;
        this.fg_pct_home = fg_pct_home;
        this.ft_pct_home = ft_pct_home;
        this.fg3_pct_home = fg3_pct_home;
        this.ast_home = ast_home;
        this.reb_home = reb_home;
        this.home_team_wins = home_team_wins;
        this.padding = new byte[7]; // 7 bytes of padding

    }

    // Method to serialize the Record into a byte array
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(Block.RECORD_SIZE); // 32 bytes
        buffer.putInt(game_date_est);
        buffer.putInt(team_id_home);
        buffer.putShort(pts_home);
        buffer.putFloat(fg_pct_home);
        buffer.putFloat(ft_pct_home);
        buffer.putFloat(fg3_pct_home);
        buffer.put(ast_home);
        buffer.put(reb_home);
        buffer.put(home_team_wins);
        buffer.put(padding); // Add 7 bytes of padding

        return buffer.array();
    }

    // Constructor to deserialize a Record from a byte array
    public Record(byte[] recordData) {
        ByteBuffer buffer = ByteBuffer.wrap(recordData);
        this.game_date_est = buffer.getInt();
        this.team_id_home = buffer.getInt();
        this.pts_home = buffer.getShort();
        this.fg_pct_home = buffer.getFloat();
        this.ft_pct_home = buffer.getFloat();
        this.fg3_pct_home = buffer.getFloat();
        this.ast_home = buffer.get();
        this.reb_home = buffer.get();
        this.home_team_wins = buffer.get();
        this.padding = new byte[7]; // Skip 7 bytes of padding
        buffer.get(this.padding);
    }

    // Getter for game_date_est
    public int getGame_date_est() {
        return game_date_est;
    }

    // Getter for team_id_home
    public int getTeam_id_home() {
        return team_id_home;
    }

    // Getter for pts_home
    public short getPts_home() {
        return pts_home;
    }

    // Getter for fg_pct_home
    public float getFg_pct_home() {
        return fg_pct_home;
    }

    // Getter for ft_pct_home
    public float getFt_pct_home() {
        return ft_pct_home;
    }

    // Getter for fg3_pct_home
    public float getFg3_pct_home() {
        return fg3_pct_home;
    }

    // Getter for ast_home
    public byte getAst_home() {
        return ast_home;
    }

    // Getter for reb_home
    public byte getReb_home() {
        return reb_home;
    }

    // Getter for home_team_wins
    public byte getHome_team_wins() {
        return home_team_wins;
    }

    public String getUniqueId() {
        return Integer.valueOf(game_date_est).toString() + Integer.valueOf(team_id_home).toString();
    }
}
