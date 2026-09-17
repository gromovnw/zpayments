package it.gromov.zpayments.util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class ConsoleCommandExecutor {

    private static final ConsoleCommandSender REAL_CONSOLE = Bukkit.getConsoleSender();
    private static final OutputHandler HANDLER = new OutputHandler(REAL_CONSOLE);
    private static final ConsoleCommandSender PROXY = (ConsoleCommandSender) Proxy.newProxyInstance(
            ConsoleCommandSender.class.getClassLoader(),
            new Class<?>[]{ConsoleCommandSender.class},
            HANDLER
    );

    private ConsoleCommandExecutor() {
    }

    public static synchronized String execute(String command) {
        HANDLER.clear();
        try {
            Bukkit.dispatchCommand(PROXY, command);
        } catch (Exception exception) {
            return "EXCEPTION: " + exception.getMessage();
        }
        return HANDLER.getOutput();
    }

    private static final class OutputHandler implements InvocationHandler {

        private final ConsoleCommandSender delegate;
        private final StringBuilder output = new StringBuilder();

        private OutputHandler(ConsoleCommandSender delegate) {
            this.delegate = delegate;
        }

        void clear() {
            output.setLength(0);
        }

        String getOutput() {
            return output.toString();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("sendMessage".equals(method.getName()) && args != null) {
                for (Object argument : args) {
                    appendArgument(argument);
                }
                return null;
            }
            return method.invoke(delegate, args);
        }

        private void appendArgument(Object argument) {
            if (argument instanceof String) {
                output.append((String) argument).append('\n');
            } else if (argument instanceof String[]) {
                for (String line : (String[]) argument) {
                    output.append(line).append('\n');
                }
            }
        }
    }
}
