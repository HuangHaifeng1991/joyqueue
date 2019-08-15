package io.chubao.joyqueue.network.transport.command.support;

import io.chubao.joyqueue.network.transport.command.Command;
import io.chubao.joyqueue.network.transport.command.handler.CommandHandler;
import io.chubao.joyqueue.network.transport.command.handler.ExceptionHandler;
import io.chubao.joyqueue.network.transport.command.handler.filter.CommandHandlerFilter;
import io.chubao.joyqueue.network.transport.command.handler.filter.CommandHandlerFilterFactory;
import io.chubao.joyqueue.network.transport.command.handler.filter.CommandHandlerInvocation;
import io.chubao.joyqueue.network.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * CommandExecuteTask
 *
 * author: gaohaoxiang
 * date: 2018/8/14
 */
public class CommandExecuteTask implements Runnable {

    protected static final Logger logger = LoggerFactory.getLogger(CommandExecuteTask.class);

    private Transport transport;
    private Command request;
    private CommandHandler commandHandler;
    private CommandHandlerFilterFactory commandHandlerFilterFactory;
    private ExceptionHandler exceptionHandler;

    public CommandExecuteTask(Transport transport, Command request, CommandHandler commandHandler, CommandHandlerFilterFactory commandHandlerFilterFactory, ExceptionHandler exceptionHandler) {
        this.transport = transport;
        this.request = request;
        this.commandHandler = commandHandler;
        this.commandHandlerFilterFactory = commandHandlerFilterFactory;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void run() {
        try {
            List<CommandHandlerFilter> commandHandlerFilters = commandHandlerFilterFactory.getFilters();
            CommandHandlerInvocation commandHandlerInvocation = new CommandHandlerInvocation(transport, request, commandHandler, commandHandlerFilters);
            Command response = commandHandlerInvocation.invoke();

            if (response != null) {
                transport.acknowledge(request, response);
            }
        } catch (Throwable t) {
            logger.error("command handler exception, tratnsport: {}, command: {}", transport, request, t);

            if (exceptionHandler != null) {
                exceptionHandler.handle(transport, request, t);
            }
        }
    }
}