package org.sjarvela.mollify.client.ui.common.grid;

import java.util.Comparator;

public interface GridComparator<T> extends Comparator<T> {
	GridColumn getColumn();

	Sort getSort();
}
