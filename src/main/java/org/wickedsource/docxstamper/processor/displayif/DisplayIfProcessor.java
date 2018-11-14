package org.wickedsource.docxstamper.processor.displayif;

import java.util.ArrayList;
import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.wickedsource.docxstamper.api.coordinates.ParagraphCoordinates;
import org.wickedsource.docxstamper.api.coordinates.TableCellCoordinates;
import org.wickedsource.docxstamper.api.coordinates.TableCoordinates;
import org.wickedsource.docxstamper.api.coordinates.TableRowCoordinates;
import org.wickedsource.docxstamper.processor.BaseCommentProcessor;
import org.wickedsource.docxstamper.processor.CommentProcessingException;
import org.wickedsource.docxstamper.util.ObjectDeleter;

public class DisplayIfProcessor extends BaseCommentProcessor implements IDisplayIfProcessor {

	private List<ParagraphCoordinates> paragraphsToBeRemoved = new ArrayList<>();

	private List<TableCoordinates> tablesToBeRemoved = new ArrayList<>();

	private List<TableRowCoordinates> tableRowsToBeRemoved = new ArrayList<>();
	
	private List<TableCellCoordinates> tableCellsToBeRemoved = new ArrayList<>();;

	@Override
	public void commitChanges(WordprocessingMLPackage document) {
		ObjectDeleter deleter = new ObjectDeleter(document);
		removeParagraphs(deleter);
		removeTables(deleter);
		removeTableRows(deleter);
		removeTableCells(deleter);
	}

	@Override
	public void reset() {
		paragraphsToBeRemoved = new ArrayList<>();
		tablesToBeRemoved = new ArrayList<>();
		tableRowsToBeRemoved = new ArrayList<>();
		tableCellsToBeRemoved = new ArrayList<>();
	}

	private void removeParagraphs(ObjectDeleter deleter) {
		for (ParagraphCoordinates pCoords : paragraphsToBeRemoved) {
			deleter.deleteParagraph(pCoords);
		}
	}

	private void removeTables(ObjectDeleter deleter) {
		for (TableCoordinates tCoords : tablesToBeRemoved) {
			deleter.deleteTable(tCoords);
		}
	}

	private void removeTableRows(ObjectDeleter deleter) {
		for (TableRowCoordinates rCoords : tableRowsToBeRemoved) {
			deleter.deleteTableRow(rCoords);
		}
	}
	
	private void removeTableCells(ObjectDeleter deleter) {
		for (TableCellCoordinates cCoords : tableCellsToBeRemoved) {
			deleter.deleteTableCell(cCoords);
		}
	}

	@Override
	public void displayParagraphIf(Boolean condition) {
		if (!condition) {
			ParagraphCoordinates coords = getCurrentParagraphCoordinates();
			paragraphsToBeRemoved.add(coords);
		}
	}

	@Override
	public void displayTableIf(Boolean condition) {
		if (!condition) {
			ParagraphCoordinates pCoords = getCurrentParagraphCoordinates();
			if (pCoords.getParentTableCellCoordinates() == null ||
					pCoords.getParentTableCellCoordinates().getParentTableRowCoordinates() == null ||
					pCoords.getParentTableCellCoordinates().getParentTableRowCoordinates().getParentTableCoordinates() == null) {
				throw new CommentProcessingException("Paragraph is not within a table!", pCoords);
			}
			tablesToBeRemoved.add(pCoords.getParentTableCellCoordinates().getParentTableRowCoordinates().getParentTableCoordinates());
		}
	}

	@Override
	public void displayTableRowIf(Boolean condition) {
		if (!condition) {
			ParagraphCoordinates pCoords = getCurrentParagraphCoordinates();
			if (pCoords.getParentTableCellCoordinates() == null ||
					pCoords.getParentTableCellCoordinates().getParentTableRowCoordinates() == null) {
				throw new CommentProcessingException("Paragraph is not within a table!", pCoords);
			}
			tableRowsToBeRemoved.add(pCoords.getParentTableCellCoordinates().getParentTableRowCoordinates());
		}
	}

	@Override
	public void displayTableCellIf(Boolean condition) {
		if (!condition) {
			ParagraphCoordinates pCoords = getCurrentParagraphCoordinates();
			if (pCoords.getParentTableCellCoordinates() == null ) {
				throw new CommentProcessingException("Paragraph is not within a table!", pCoords);
			}
			tableCellsToBeRemoved.add(pCoords.getParentTableCellCoordinates());
		}
	}
	
	
}
