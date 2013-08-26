/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.operation.protocol;

import org.mongodb.CommandResult;
import org.mongodb.Document;
import org.mongodb.Encoder;
import org.mongodb.MongoNamespace;
import org.mongodb.WriteConcern;
import org.mongodb.connection.BufferProvider;
import org.mongodb.connection.Channel;
import org.mongodb.connection.ServerDescription;
import org.mongodb.diagnostics.Loggers;
import org.mongodb.operation.Remove;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.mongodb.assertions.Assertions.notNull;
import static org.mongodb.operation.OperationHelpers.getMessageSettings;

public class DeleteCommandProtocol extends WriteCommandProtocol {

    private static final Logger LOGGER = Loggers.getLogger("protocol.delete");

    private final List<Remove> removes;
    private final Encoder<Document> queryEncoder;

    public DeleteCommandProtocol(final MongoNamespace namespace, final WriteConcern writeConcern, final List<Remove> removes,
                                 final Encoder<Document> queryEncoder, final BufferProvider bufferProvider,
                                 final ServerDescription serverDescription, final Channel channel, final boolean closeChannel) {
        super(namespace, writeConcern,  bufferProvider, serverDescription, channel, closeChannel);
        this.removes = notNull("removes", removes);
        this.queryEncoder = notNull("queryEncoder", queryEncoder);
    }

    @Override
    public CommandResult execute() {
        LOGGER.fine(format("Deleting documents from namespace %s on connection [%s] to server %s", getNamespace(),
                getConnection().getId(), getConnection().getServerAddress()));
        CommandResult commandResult = super.execute();
        LOGGER.fine("Delete completed");
        return commandResult;
    }

    @Override
    protected DeleteCommandMessage createRequestMessage() {
        return new DeleteCommandMessage(getNamespace(), getWriteConcern(), removes, queryEncoder,
                getMessageSettings(getServerDescription()));
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
