package calibration.view;

import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDSSFileAccess;
import hec.heclib.dss.HecDssCatalog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

public class BrowseDss extends JDialog {
	private static final Logger logger = Logger.getLogger(BrowseDss.class.getName());

	private final String[] paths;
	private List<DSSPathname> dssPathnames;

	private JComboBox<String> partAFilter;
	private JComboBox<String> partBFilter;
	private JComboBox<String> partCFilter;
	private JComboBox<String> partDFilter;
	private JComboBox<String> partEFilter;
	private JComboBox<String> partFFilter;

	private JTable table;
	private JButton setButton;

	public BrowseDss(String pathToDssFile) {
		HecDSSFileAccess.setDefaultDSSFileName(pathToDssFile);
		HecDssCatalog catalog = new HecDssCatalog();
		CondensedReference[] references = catalog.getCondensedCatalog(null);
		paths = new String[references.length];
		for (int i = 0; i < references.length; i++) {
			paths[i] = references[i].toString();
		}

		initialize();

		add(filterPanel(), BorderLayout.NORTH);
		add(table(), BorderLayout.CENTER);
		add(buttonPanel(), BorderLayout.SOUTH);

		setSize(800, 600);
		setTitle("Select Pathname From HEC-DSS File");

		try {
			URL url = Objects.requireNonNull(getClass().getResource("/graph.png"));
			BufferedImage icon = ImageIO.read(url);
			setIconImage(icon);
		} catch (IOException e) {
			logger.warning(e::getMessage);
		}

		setModal(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				table.clearSelection();
			}
		});
	}

	private void initialize() {
		Set<String> aParts = new TreeSet<>();
		Set<String> bParts = new TreeSet<>();
		Set<String> cParts = new TreeSet<>();
		Set<String> dParts = new TreeSet<>();
		Set<String> eParts = new TreeSet<>();
		Set<String> fParts = new TreeSet<>();

		dssPathnames = new ArrayList<>();
		for (String path : paths) {
			DSSPathname dssPathname = new DSSPathname(path);
			dssPathnames.add(dssPathname);
			aParts.add(dssPathname.aPart());
			bParts.add(dssPathname.bPart());
			cParts.add(dssPathname.cPart());
			dParts.add(dssPathname.dPart());
			eParts.add(dssPathname.ePart());
			fParts.add(dssPathname.fPart());
		}

		partAFilter = new JComboBox<>(new Vector<>(aParts));
		partAFilter.insertItemAt("", 0);
		partAFilter.setSelectedItem("");
		partAFilter.addActionListener(e -> filter());

		partBFilter = new JComboBox<>(new Vector<>(bParts));
		partBFilter.insertItemAt("", 0);
		partBFilter.setSelectedItem("");
		partBFilter.addActionListener(e -> filter());

		partCFilter = new JComboBox<>(new Vector<>(cParts));
		partCFilter.insertItemAt("", 0);
		partCFilter.setSelectedItem("");
		partCFilter.addActionListener(e -> filter());

		partDFilter = new JComboBox<>(new Vector<>(dParts));
		partDFilter.insertItemAt("", 0);
		partDFilter.setSelectedItem("");
		partDFilter.addActionListener(e -> filter());

		partEFilter = new JComboBox<>(new Vector<>(eParts));
		partEFilter.insertItemAt("", 0);
		partEFilter.setSelectedItem("");
		partEFilter.addActionListener(e -> filter());

		partFFilter = new JComboBox<>(new Vector<>(fParts));
		partFFilter.insertItemAt("", 0);
		partFFilter.setSelectedItem("");
		partFFilter.addActionListener(e -> filter());
	}
	
	private Component filterPanel() {
		GridBagLayout dssGridLayout = new GridBagLayout();
		JPanel dssPartsPanel = new JPanel(dssGridLayout);

		GridBagConstraints dssPartsConstraints = new GridBagConstraints();
		dssPartsConstraints.fill = GridBagConstraints.HORIZONTAL;

		dssPartsConstraints.gridy = 0;
		dssPartsConstraints.gridx = 0;
		dssPartsConstraints.weightx = 1;
		JPanel partAPanel = new JPanel();
		partAPanel.setLayout(new BoxLayout(partAPanel, BoxLayout.X_AXIS));
		partAPanel.add(new JLabel("A: "));
		partAPanel.add(partAFilter);
		dssPartsPanel.add(partAPanel, dssPartsConstraints);

		dssPartsConstraints.gridy = 0;
		dssPartsConstraints.gridx = 1;
		dssPartsConstraints.weightx = 1;
		JPanel partBPanel = new JPanel();
		partBPanel.setLayout(new BoxLayout(partBPanel, BoxLayout.X_AXIS));
		partBPanel.add(Box.createRigidArea(new Dimension(5,0)));
		partBPanel.add(new JLabel("B: "));
		partBPanel.add(partBFilter);
		dssPartsPanel.add(partBPanel, dssPartsConstraints);

		dssPartsConstraints.gridy = 0;
		dssPartsConstraints.gridx = 2;
		dssPartsConstraints.weightx = 1;
		JPanel partCPanel = new JPanel();
		partCPanel.setLayout(new BoxLayout(partCPanel, BoxLayout.X_AXIS));
		partCPanel.add(Box.createRigidArea(new Dimension(5,0)));
		partCPanel.add(new JLabel("C: "));
		partCPanel.add(partCFilter);
		dssPartsPanel.add(partCPanel, dssPartsConstraints);

		dssPartsConstraints.gridy = 1;
		dssPartsConstraints.gridx = 0;
		dssPartsConstraints.weightx = 1;
		dssPartsConstraints.insets = new Insets(10,0,0,0);
		JPanel partDPanel = new JPanel();
		partDPanel.setLayout(new BoxLayout(partDPanel, BoxLayout.X_AXIS));
		partDPanel.add(new JLabel("D: "));
		partDPanel.add(partDFilter);
		dssPartsPanel.add(partDPanel, dssPartsConstraints);

		dssPartsConstraints.gridy = 1;
		dssPartsConstraints.gridx = 1;
		dssPartsConstraints.weightx = 1;
		JPanel partEPanel = new JPanel();
		partEPanel.setLayout(new BoxLayout(partEPanel, BoxLayout.X_AXIS));
		partEPanel.add(Box.createRigidArea(new Dimension(5,0)));
		partEPanel.add(new JLabel("E: "));
		partEPanel.add(partEFilter);
		dssPartsPanel.add(partEPanel, dssPartsConstraints);

		dssPartsConstraints.gridy = 1;
		dssPartsConstraints.gridx = 2;
		dssPartsConstraints.weightx = 1;
		JPanel partFPanel = new JPanel();
		partFPanel.setLayout(new BoxLayout(partFPanel, BoxLayout.X_AXIS));
		partFPanel.add(Box.createRigidArea(new Dimension(5,0)));
		partFPanel.add(new JLabel("F: "));
		partFPanel.add(Box.createRigidArea(new Dimension(1,0)));
		partFPanel.add(partFFilter);
		dssPartsPanel.add(partFPanel, dssPartsConstraints);

		dssPartsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		return dssPartsPanel;
	}

	private Component table() {
		table = new JTable();
		table.setModel(new AbstractTableModel() {
			final String[] columnNames = {
					"Number",
					"Part A",
					"Part B",
					"Part C",
					"Part D / range",
					"Part E",
					"Part F"
			};

			@Override
			public String getColumnName(int column) {
				return columnNames[column];
			}

			@Override
			public int getRowCount() {
				return dssPathnames.size();
			}

			@Override
			public int getColumnCount() {
				return columnNames.length;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				DSSPathname dssPathname = dssPathnames.get(rowIndex);
				switch (columnIndex) {
					case 0:
						return rowIndex + 1;
					case 1:
						return dssPathname.aPart();
					case 2:
						return dssPathname.bPart();
					case 3:
						return dssPathname.cPart();
					case 4:
						return dssPathname.dPart();
					case 5:
						return dssPathname.ePart();
					case 6:
						return dssPathname.fPart();
					default:
						return null;
				}
			}
		});

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		table.getTableHeader().setDefaultRenderer(new MultiLineHeaderRenderer());
//
//		TableColumnResizer.autoResizeColumnWidth(table);

		table.getSelectionModel().addListSelectionListener(e ->
				setButton.setEnabled(!table.getSelectionModel().isSelectionEmpty())
		);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					dispose();
				}
			}
		});

		return new JScrollPane(table);
	}

	private Component buttonPanel() {
		setButton = new JButton("Set Pathname");
		setButton.setEnabled(!table.getSelectionModel().isSelectionEmpty());
		setButton.addActionListener(e -> dispose());

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(setButton, BorderLayout.CENTER);

		return buttonPanel;
	}

	private void filter() {
		dssPathnames.clear();

		String partASelection = String.valueOf(partAFilter.getSelectedItem());
		String partBSelection = String.valueOf(partBFilter.getSelectedItem());
		String partCSelection = String.valueOf(partCFilter.getSelectedItem());
		String partDSelection = String.valueOf(partDFilter.getSelectedItem());
		String partESelection = String.valueOf(partEFilter.getSelectedItem());
		String partFSelection = String.valueOf(partFFilter.getSelectedItem());

		for (String path : paths) {
			DSSPathname dssPathname = new DSSPathname(path);
			if (!partASelection.isEmpty() && !partASelection.equals(dssPathname.aPart())
					|| !partBSelection.isEmpty() && !partBSelection.equals(dssPathname.bPart())
					|| !partCSelection.isEmpty() && !partCSelection.equals(dssPathname.cPart())
					|| !partDSelection.isEmpty() && !partDSelection.equals(dssPathname.dPart())
					|| !partESelection.isEmpty() && !partESelection.equals(dssPathname.ePart())
					|| !partFSelection.isEmpty() && !partFSelection.equals(dssPathname.fPart())) {
				continue;
			}
			dssPathnames.add(dssPathname);
		}

		table.tableChanged(new TableModelEvent(table.getModel()));
	}

	public String getSelection() {
		int selectedRow = table.getSelectedRow();
		return selectedRow >= 0 ? dssPathnames.get(selectedRow).getPathname() : null;
	}
}
