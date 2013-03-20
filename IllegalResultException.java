/* 4xxや5xx系の結果になった時に投げられる例外 */
class IllegalResultException extends Exception  {
    public IllegalResultException(String message) {
        super(message);
    }

    public IllegalResultException() {
        this("Unknown Error");
    }
}
