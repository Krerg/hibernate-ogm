/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.datastore.document.association.spi;

import org.hibernate.ogm.datastore.document.association.spi.KeyedAssociationRow.AssociationRowAccessor;
import org.hibernate.ogm.grid.AssociationKey;
import org.hibernate.ogm.util.impl.Contracts;

/**
 * Base class for {@link AssociationRowFactory} implementations which support association rows stored as key/value
 * tuples as well as rows stored as collections of single values.
 * <p>
 * The latter form may be used to persist association rows with exactly one column (which is the case for collections of
 * simple values such as {@code int}s, {@code String} s etc. as well as associations based on non-composite keys). In
 * this case a row object of type {@code R} will be created using the column value and the single row key column which
 * is not part of the association key.
 * <p>
 * For rows with more than one column it is assumed that they are already of type {@code R} and they are thus passed
 * through as is.
 *
 * @author Gunnar Morling
 * @param <R> The type of key/value association rows supported by this factory.
 */
public abstract class UnkeyedValueAwareAssociationRowFactory<R> implements AssociationRowFactory {

	/**
	 * The type of key/value association rows supported by this factory; This basically corresponds to {@code Class<R>}
	 * but this form is used to support parameterized types such as {@code Map}.
	 */
	private final Class<?> associationRowType;

	protected UnkeyedValueAwareAssociationRowFactory(Class<?> associationRowType) {
		this.associationRowType = associationRowType;
	}

	@Override
	@SuppressWarnings("unchecked")
	public KeyedAssociationRow<?> createAssociationRow(AssociationKey associationKey, Object row) {
		R rowObject = null;

		if ( associationRowType.isInstance( row ) ) {
			rowObject = (R) row;
		}
		else {
			String columnName = associationKey.getMetadata().getSingleRowKeyColumnNotContainedInAssociationKey();
			Contracts.assertNotNull( columnName, "columnName" );
			rowObject = getSingleColumnRow( columnName, row );
		}

		return new KeyedAssociationRow<R>( associationKey, getAssociationRowAccessor(), rowObject );
	}

	/**
	 * Creates a row object with the given column name and value.
	 */
	protected abstract R getSingleColumnRow(String columnName, Object value);

	/**
	 * Returns the {@link AssociationRowAccessor} to be used to obtain values from the {@link KeyedAssociationRow}
	 * created by this factory.
	 */
	protected abstract AssociationRowAccessor<R> getAssociationRowAccessor();
}