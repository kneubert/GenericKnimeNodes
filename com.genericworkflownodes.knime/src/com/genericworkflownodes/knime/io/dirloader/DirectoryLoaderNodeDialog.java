package com.genericworkflownodes.knime.io.dirloader;


import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;


/**
 * <code>NodeDialog</code> for the "IndexReader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Kerstin Neubert, FU Berlin
 */
public class DirectoryLoaderNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the IndexReader node.
     */
    protected DirectoryLoaderNodeDialog() {
    	
        super();
        addDialogComponent(new DialogComponentFileChooser(
                new SettingsModelString(DirectoryLoaderNodeModel.CFG_DIRECTORYNAME,""), 
                "MimeFileImporterNodeDialog",
        		JFileChooser.OPEN_DIALOG,
        		true));
        
    	
    }
    
    

    
    
}

