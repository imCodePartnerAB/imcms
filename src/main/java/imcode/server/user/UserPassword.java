package imcode.server.user;

public abstract class UserPassword {

    public enum Type {
        PLAIN, ENCRYPTED, RESET
    }

    private final Type type;

    private UserPassword(Type type) {
        this.type = type;
    }

    public final Type type() {
        return type;
    }

    public final static class Plain extends UserPassword {
        private Plain() {
            super(Type.PLAIN);
        }
    }

    public final static class Encrypted extends UserPassword {
        private Encrypted() {
            super(Type.ENCRYPTED);
        }
    }

    public final static class Reset extends UserPassword {
        private Reset() {
            super(Type.RESET);
        }
    }

    public static Plain plain() {
        return new Plain();
    }

    public static Encrypted encrypted() {
        return new Encrypted();
    }

    public static Reset reset() {
        return new Reset();
    }

    public static void main(String[] args) {
        UserPassword up = UserPassword.encrypted();

        switch (up.type()) {
            case ENCRYPTED:
                UserPassword.Encrypted ep = (UserPassword.Encrypted) up;
                break;

            case PLAIN:
                UserPassword.Plain pp = (UserPassword.Plain) up;
                break;

            case RESET:
                UserPassword.Reset rp = (UserPassword.Reset) up;
                break;
        }
    }
}