package com.droidkit.actors;

import com.droidkit.actors.mailbox.Mailbox;
import com.droidkit.actors.mailbox.MailboxesQueue;

/**
 * Created by ex3ndr on 07.09.14.
 */
public interface MailboxCreator {
    public Mailbox createMailbox(MailboxesQueue queue);
}
