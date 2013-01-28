/**
 * Copyright 2012-2013 Rafal Lewczuk <rafal.lewczuk@jitlogic.com>
 * <p/>
 * This is free software. You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jitlogic.zorka.agent.test.rankproc;

import com.jitlogic.zorka.agent.rankproc.QueryDef;
import com.jitlogic.zorka.agent.rankproc.QueryLister;
import com.jitlogic.zorka.agent.rankproc.QueryResult;
import com.jitlogic.zorka.agent.test.support.TestJmx;
import com.jitlogic.zorka.agent.test.support.ZorkaFixture;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.management.ObjectName;
import java.util.List;

public class JmxQueryUnitTest extends ZorkaFixture {

    private TestJmx jmx1;
    private TestJmx jmx2;

    @Before
    public void createSomeMBeans() throws Exception {
        jmx1 = makeTestJmx("test:name=bean1,type=TestJmx", 10, 10, "oja", "woja", "aja", "waja", "uja", "wuja");
        jmx2 = makeTestJmx("test:name=bean2,type=TestJmx", 10, 10, "oja", "woja", "aja", "waja", "eja", "weja");
    }


    @Test
    public void testTrivialSearchAttrOnly() {
        QueryLister lister = new QueryLister(mBeanServerRegistry,
                new QueryDef("test", "test:type=TestJmx,*", "name"));

        List<QueryResult> results = lister.list();

        Assert.assertEquals(2, results.size());

        Assert.assertEquals("bean1", results.get(0).getAttr("name"));
    }


    @Test
    public void testTrivialSearchAndGetSingleAttr() {
        QueryLister lister = new QueryLister(mBeanServerRegistry,
                new QueryDef("test", "test:type=TestJmx,*", "name").get("Nom", "Nom"));

        List<QueryResult> results = lister.list();

        Assert.assertEquals(2, results.size());

        Assert.assertEquals("Nom", results.get(0).getAttr("Nom"));
        Assert.assertEquals(10L, results.get(0).getResult());
    }


    @Test
    public void testSearchAndGetMultipleAttrs() {
        QueryLister lister = new QueryLister(mBeanServerRegistry,
                new QueryDef("test", "test:type=TestJmx,*", "name").list("*", "Attr"));

        List<QueryResult> results = lister.list();

        Assert.assertEquals(6, results.size());

        Assert.assertEquals("Nom", results.get(0).getAttr("Attr"));
        Assert.assertEquals("Div", results.get(1).getAttr("Attr"));
        Assert.assertEquals(10L, results.get(0).getResult());
    }


    @Test
    public void testSearchAndGetMultiSecondLevelAttr() {
        QueryLister lister = new QueryLister(mBeanServerRegistry,
                new QueryDef("test", "test:type=TestJmx,*", "name").get("StrMap").list("*", "Attr"));

        List<QueryResult> results = lister.list();

        Assert.assertEquals(6, results.size());

        Assert.assertEquals("aja", results.get(0).getAttr("Attr"));
        Assert.assertEquals("oja", results.get(1).getAttr("Attr"));
    }


    @Test
    public void testSearchAndGetMultipleSecondLevelAttr() {
        QueryLister lister = new QueryLister(mBeanServerRegistry,
                new QueryDef("test", "test:type=TestJmx,*", "name").get("StrMap").get("oja", "Attr"));

        List<QueryResult> results = lister.list();

        Assert.assertEquals(2, results.size());

        Assert.assertEquals("oja", results.get(0).getAttr("Attr"));
        Assert.assertEquals("oja", results.get(1).getAttr("Attr"));
    }


    private TestJmx makeTestJmx(String name, long nom, long div, String...md) throws Exception {
        TestJmx bean = new TestJmx();

        bean.setNom(nom); bean.setDiv(div);

        for (int i = 1; i < md.length; i += 2) {
            bean.put(md[i-1], md[i]);
        }

        testMbs.registerMBean(bean, new ObjectName(name));

        return bean;
    }
}
