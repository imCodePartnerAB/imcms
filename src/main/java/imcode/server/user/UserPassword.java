package imcode.server.user;

public abstract class UserPassword {

    private final Type type;

    private UserPassword(Type type) {
        this.type = type;
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

    public final Type type() {
        return type;
    }

    public enum Type {
        PLAIN, ENCRYPTED, RESET
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
}