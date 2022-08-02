package relationExtraction;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class relationsTableModel extends AbstractTableModel {
	
	private List<relations> cityList;
	
	relationsTableModel(List<relations> cityList) {
		this.cityList = cityList;
	}
	
	private String[] colNames = {relationsViewer.SUBJECT,
			relationsViewer.VERB, relationsViewer.OBJECT, relationsViewer.SENTENCE};
	
	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}

	@Override
	public int getColumnCount() {
		return colNames.length;
	}
	
	@Override
	public int getRowCount() {
		return cityList.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if(row < 0 || row >= cityList.size()) return null;
		relations obj = cityList.get(row);
        switch(column)
        {
            case 0: return obj.getSubject();
            case 1: return obj.getVerb();
            case 2: return obj.getObject();
            case 3: return obj.getSentence();
            default: return null;
        }
	}


}
