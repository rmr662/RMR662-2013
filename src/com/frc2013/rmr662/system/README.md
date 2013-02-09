How to add methods to HardwarePool:
===================================

For hardwares that are not wrapped, use this template:


```java
public synchronized <TYPE> get<TYPE>( <CHANNELS> ) {
	if (exists(<TYPE>.class, <CHANNELS|UNUSED>)) {
		return (<TYPE>) getFoundObject();
	} else {
		<TYPE> obj = <CONSTRUCTOR>;
		addObject(obj);
		return obj;
	}
}
```

When passing channels to `exists(...)`, put UNUSED for any channels that are not used for that specific component.

For hardwares that are wrapped, see the `getDigitalInput(...)` method for an example.