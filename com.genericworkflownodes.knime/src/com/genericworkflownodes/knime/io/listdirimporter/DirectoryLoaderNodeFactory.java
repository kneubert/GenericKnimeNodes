package com.genericworkflownodes.knime.io.listdirimporter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndexReader" Node.
 * 
 *
 * @author Kerstin Neubert, FU Berlin
 */
public class DirectoryLoaderNodeFactory 
        extends NodeFactory<DirectoryLoaderNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectoryLoaderNodeModel createNodeModel() {
        return new DirectoryLoaderNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<DirectoryLoaderNodeModel> createNodeView(final int viewIndex,
            final DirectoryLoaderNodeModel nodeModel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new DirectoryLoaderNodeDialog();
    }

}

