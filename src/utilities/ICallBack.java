package utilities;

import ll.gates.AGate;

import java.util.Map;

/**
 * Created by gvoiron on 18/10/17.
 * Time : 20:33
 */
@FunctionalInterface
public interface ICallBack {

    void apply(long t, Map<AGate, Boolean> gates);

}
