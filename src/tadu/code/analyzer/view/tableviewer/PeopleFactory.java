package tadu.code.analyzer.view.tableviewer;

import java.util.ArrayList;
import java.util.List;

public class PeopleFactory {

	public static List<PeopleEntity> getPeoples() {
		List<PeopleEntity> list = new ArrayList<PeopleEntity>();
		{
			PeopleEntity o = new PeopleEntity();
			o.setId(new Long(1));
			o.setServletName("BookAction.java");
			o.setServletURL("book/");
			o.setChangedContent("cover.css");
			list.add(o);
		}

		{
			PeopleEntity o = new PeopleEntity();
			o.setId(new Long(2));
			o.setServletName("BookAction2.java");
			o.setServletURL("book/");
			o.setChangedContent("cover.css");
			list.add(o);
		}
		{
			PeopleEntity o = new PeopleEntity();
			o.setId(new Long(3));
			o.setServletName("BookAction3.java");
			o.setServletURL("book/");
			o.setChangedContent("cover.css");
			list.add(o);
		}
		{
			PeopleEntity o = new PeopleEntity();
			o.setId(new Long(4));
			o.setServletName("BookAction4.java");
			o.setServletURL("book/");
			o.setChangedContent("cover.css");
			list.add(o);
		}
		return list;
	}
}