/**
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.nodes.io.listdirimporter;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;

/**
 * <code>NodeDialog</code> for the "ListMimeFileImporter" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author roettig
 */
public class ListDirImporterNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring ListMimeFileImporter node dialog. This is just a
     * suggestion to demonstrate possible default dialog components.
     * 
     * @param obj
     */
    protected ListDirImporterNodeDialog() {
        super();
        addDialogComponent(new DialogComponentMultiDirChooser(
                new SettingsModelStringArray(
                        ListDirImporterNodeModel.CFG_DIRECTORYNAME,
                        new String[] {})));
 
    }
}
