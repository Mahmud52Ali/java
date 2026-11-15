package coursehub.pipeline;

@FunctionalInterface
public interface RequestHandler {
	void process(Request request) throws Exception;
}
