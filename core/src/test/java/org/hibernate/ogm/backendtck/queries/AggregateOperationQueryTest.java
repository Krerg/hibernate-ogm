/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.backendtck.queries;

import static org.fest.assertions.Assertions.assertThat;
import static org.hibernate.ogm.utils.GridDialectType.HASHMAP;
import static org.hibernate.ogm.utils.GridDialectType.INFINISPAN;
import static org.hibernate.ogm.utils.GridDialectType.INFINISPAN_REMOTE;
import static org.hibernate.ogm.utils.GridDialectType.NEO4J_EMBEDDED;
import static org.hibernate.ogm.utils.GridDialectType.NEO4J_REMOTE;

import org.hibernate.ogm.utils.SkipByGridDialect;
import org.hibernate.ogm.utils.jpa.OgmJpaTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Davide D'Alto
 * @author Aleksandr Mylnikov
 */
@SkipByGridDialect(
		value = { HASHMAP, INFINISPAN, INFINISPAN_REMOTE, NEO4J_EMBEDDED, NEO4J_REMOTE },
		comment = "We need to improve hql parser for dialects")
public class AggregateOperationQueryTest extends OgmJpaTestCase {

	@Before
	public void populateDb() {
		inTransaction( em -> {
			em.persist( new Author( 1l, "Josh" ) );
			em.persist( new Author( 2l, "Ela" ) );
			em.persist( new Author( 4l, "Ela", 20 ) );
			em.persist( new Author( 3l, "Ela", 34 ) );

		} );
	}

	@After
	@Override
	public void removeEntities() throws Exception {
		super.removeEntities();
	}

	@Test
	public void shouldCountEntities() {
		inTransaction( em -> {
			Integer result = (Integer) em.createQuery( "SELECT COUNT(author.age) FROM Author author" ).getSingleResult();
			assertThat( result ).isEqualTo( 4 );
		} );
	}

	@Test
	public void shouldCountEntitiesWithCondition() {
		inTransaction( em -> {
			Integer result = (Integer) em.createQuery( "select count(*) from Author a WHERE id = :id" ).setParameter( "id", 3l ).getSingleResult();
			assertThat( result ).isEqualTo( 1 );
		} );
	}

	@Test
	public void shouldAggregateSumEntitiesWithCondition() {
		inTransaction( em -> {
			Integer result = (Integer) em.createQuery( "select sum(a.age) from Author a WHERE id = :id" ).setParameter( "id", 3l ).getSingleResult();
			assertThat( result ).isEqualTo( 34 );
		} );
	}

	@Test
	public void shouldAggregateSumEntitiesWithCondition2() {
		inTransaction( em -> {
			Integer result = (Integer) em.createQuery( "select sum(a.age) from Author a" ).getSingleResult();
			assertThat( result ).isEqualTo( 54 );
		} );
	}

	@Test
	public void shouldAggregateSumEntitiesWithCondition3() {
		inTransaction( em -> {
			Integer result = (Integer) em.createQuery( "select min(a.age) from Author a" ).getSingleResult();
			assertThat( result ).isEqualTo( 20 );
		} );
	}


	@Test
	public void shouldAggregateSumEntitiesWithCondition4() {
		inTransaction( em -> {
			Integer result = (Integer) em.createQuery( "select max(a.age) from Author a" ).getSingleResult();
			assertThat( result ).isEqualTo( 34 );
		} );
	}

	@Test
	public void shouldAggregateSumEntitiesWithCondition5() {
		inTransaction( em -> {
			Double result = (Double) em.createQuery( "select avg(a.age) from Author a" ).getSingleResult();
			assertThat( result ).isEqualTo( 27.0 );
		} );
	}


	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[]{ Author.class, Address.class, Hypothesis.class };
	}
}
