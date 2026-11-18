package util.resource;

public class ResourcePathFinder {
    public String getName(ImageResource type) {
        switch (type) {
            case KING_W:
                return "KingW.png";

            case KING_B:
                return  "KingB.png";

            case PAWN_W:
                return "PawnW.png";

            case PAWN_B:
                return "PawnB.png";

            case UP_PAWN_W:
                return "UpPawnW.png";

            case UP_PAWN_B:
                return "UpPawnB.png";

            case LANCE_W:
                return "LanceW.png";

            case LANCE_B:
                return "LanceB.png";
            case UP_LANCE_W:
                return "UpLanceW.png";

            case UP_LANCE_B:
                return "UpLanceB.png";

            case BISHOP_W:
                return "BishopW.png";
            case BISHOP_B:
                return "BishopB.png";

            case UP_BISHOP_W:
                return "UpBishopW.png";

            case UP_BISHOP_B:
                return "UpBishopB.png";

            case GOLDENGENERAL_W:
                return "GoldenGeneralW.png";

            case GOLDENGENERAL_B:
                return "GoldenGeneralB.png";

            case SILVERGENERAL_W:
                return "SilverGeneralW.png";

            case SILVERGENERAL_B:
                return "SilverGeneralB.png";

            case UP_SILVERGENERAL_W:
                return "UpSilverGeneralW.png";

            case UP_SILVERGENERAL_B:
                return "UpSilverGeneralB.png";

            case BACKGROUND:
                return "BACKGROUND.png";
            case BHAND:
                return "BHAND.jpg";
            case WHAND:
                return "WHAND.jpg";
            case BOARD:
                return "BOARD.jpg";
            default:
                return type.toString();
        }
    }
}
