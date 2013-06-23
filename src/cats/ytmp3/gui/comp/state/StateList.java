package cats.ytmp3.gui.comp.state;

import cats.ytmp3.dl.DownloadService;
import cats.ytmp3.dl.State;
import cats.ytmp3.dl.event.StateChangeEvent;
import cats.ytmp3.dl.event.StateChangeListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Component;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: cats
 * Date: 6/21/13
 * Time: 3:45 PM
 */
public class StateList extends JList<StateListData> implements StateChangeListener, ListSelectionListener {

    private class Renderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focused){
            final Component component = super.getListCellRendererComponent(list, value, index, selected, focused);
            if(index < 0)
                return component;
            final JLabel label = (JLabel)component;
            final StateListData stateListData = (StateListData)value;
            final String name = stateListData.getName().toLowerCase(Locale.CANADA);
            label.setToolTipText(name.equals("All") ? "Shows all of the downloads" : String.format("Shows all of the %s downloads", name));
            label.setText(String.format("%s (%d)", stateListData.getName(), map.get(stateListData)));
            label.setIcon(data[index].getIcon());
            return label;
        }
    }

    private final StateListData[] data;
    private final StateListPanel parent;
    private final Renderer renderer;

    private final Map<StateListData, Integer> map;

    public StateList(final StateListPanel parent){
        parent.getWindow().getService().addStateChangeListener(this);
        addListSelectionListener(this);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.parent = parent;

        map = new HashMap<>();

        data = new StateListData[]{
                new StateListData("All", "all.png", State.values()),
                new StateListData("Active", "active.png", State.DOWNLOADING, State.INITIALIZING),
                new StateListData("Inactive", "inactive.png", State.WAITING, State.SUCCESS, State.FAIL),
                new StateListData("Successful", "success.png", State.SUCCESS),
                new StateListData("Failed", "fail.png", State.FAIL)
        };

        for(final StateListData d : data)
            map.put(d, 0);

        setListData(data);

        renderer = new Renderer();

        setCellRenderer(renderer);
    }

    public void valueChanged(final ListSelectionEvent e){
        final Object source = e.getSource();
        if(source.equals(this)){
            final int index = getSelectedIndex();
            if(index < 0)
                return;
            parent.getWindow().getTableQueuePanel().getTable().filter(data[index].getStates());
        }
    }

    private void recalculate(final DownloadService service){
        for(final StateListData data : map.keySet())
            map.put(data, data.getCount(service));
    }

    public void onStateChange(final StateChangeEvent e){
        if(e.getDownloader().getService().equals(parent.getWindow().getService())){
            recalculate(parent.getWindow().getService());
            repaint();
        }
    }
}
