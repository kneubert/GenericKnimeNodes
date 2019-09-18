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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;


/**
 * This is the model implementation of ListDirImporter.
 * 
 * @author roettig, aiche, neubert
 */
public class ListDirImporterNodeModel extends NodeModel {

    /**
     * ID for the directoryname configuration.
     */
    static final String CFG_DIRECTORYNAME = "DIRECTORYNAME";

    /**
     * Model containing the file names and optional extension.
     */
    private SettingsModelStringArray m_directory_names = new SettingsModelStringArray(
            ListDirImporterNodeModel.CFG_DIRECTORYNAME, new String[] {});


    /**
     * Constructor for the node model.
     */
    protected ListDirImporterNodeModel() {
        super(new PortType[] {}, new PortType[] { IURIPortObject.TYPE });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_directory_names.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_directory_names.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {

        SettingsModelStringArray tmp_filenames = m_directory_names
                .createCloneWithValidatedValue(settings);

        if (tmp_filenames == null
                || tmp_filenames.getStringArrayValue().length == 0) {
            throw new InvalidSettingsException("No Files selected.");
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {

        /*
         * Upon inserting the node into a workflow, it gets configured, so at
         * least something fundamental like the file name should be checked
         */
        String[] filenames = m_directory_names.getStringArrayValue();
        if (filenames == null || filenames.length == 0) {
            throw new InvalidSettingsException("No Files selected.");
        }

        URIPortObjectSpec uri_spec = null;
        uri_spec = new URIPortObjectSpec("");


        return new PortObjectSpec[] { uri_spec };
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
        String[] filenames = m_directory_names.getStringArrayValue();

        List<URIContent> uris = new ArrayList<URIContent>();
        for (String filename : filenames) {
            File in = new File(convertToURL(filename).toURI());

            if (!in.canRead()) {
                throw new Exception("Cannot read from input file: "
                        + in.getAbsolutePath());
            }

            uris.add(new URIContent(in.toURI(),""));
           
        }
        
        return new PortObject[] { new URIPortObject(uris) }; 
    }

    
    /**
     * Extract a URL from the given String, trying different conversion
     * approaches. Inspired by CSVReaderConfig#loadSettingsInModel().
     * 
     * @param urlS
     *            The string containing the URL.
     * @return A URL object.
     * @throws InvalidSettingsException
     *             If the given string cannot be converted properly.
     */
    private URL convertToURL(String urlS) throws InvalidSettingsException {
        URL url;

        if (urlS == null) {
            throw new InvalidSettingsException("URL must not be null");
        }
        try {
            url = new URL(urlS);
        } catch (MalformedURLException e) {
            // might be a file, bug fix 3477
            File file = new File(urlS);
            try {
                url = file.toURI().toURL();
            } catch (Exception fileURLEx) {
                throw new InvalidSettingsException("Invalid URL: "
                        + e.getMessage(), e);
            }
        }

        return url;
    }
}
