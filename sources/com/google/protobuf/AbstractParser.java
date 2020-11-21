package com.google.protobuf;

import com.google.protobuf.MessageLite;

public abstract class AbstractParser<MessageType extends MessageLite> implements Parser<MessageType> {
    private UninitializedMessageException newUninitializedMessageException(MessageType messagetype) {
        if (messagetype instanceof AbstractMessageLite) {
            return ((AbstractMessageLite) messagetype).newUninitializedMessageException();
        }
        return new UninitializedMessageException(messagetype);
    }

    private MessageType checkMessageInitialized(MessageType messagetype) throws InvalidProtocolBufferException {
        if (messagetype == null || messagetype.isInitialized()) {
            return messagetype;
        }
        InvalidProtocolBufferException asInvalidProtocolBufferException = newUninitializedMessageException(messagetype).asInvalidProtocolBufferException();
        asInvalidProtocolBufferException.setUnfinishedMessage(messagetype);
        throw asInvalidProtocolBufferException;
    }

    static {
        ExtensionRegistryLite.getEmptyRegistry();
    }

    public MessageType parsePartialFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        try {
            CodedInputStream newCodedInput = byteString.newCodedInput();
            MessageType messagetype = (MessageType) ((MessageLite) parsePartialFrom(newCodedInput, extensionRegistryLite));
            try {
                newCodedInput.checkLastTagWas(0);
                return messagetype;
            } catch (InvalidProtocolBufferException e) {
                e.setUnfinishedMessage(messagetype);
                throw e;
            }
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        MessageType parsePartialFrom = parsePartialFrom(byteString, extensionRegistryLite);
        checkMessageInitialized(parsePartialFrom);
        return parsePartialFrom;
    }
}
