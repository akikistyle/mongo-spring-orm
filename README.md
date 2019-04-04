# MongoDB ORM for Spring

A simple MongoDB ORM jar for Java Spring to create DAO and services easily.

# Install

Use AntBuild to build jar.

# How to use

Create test bean such as:
```$xslt
@Document(collection = "demo")
public class DemoBean {
    @Id
    private String id;
    
    @Indexed
    private Integer idx;
    
    private String val;
    
    // getters and setters
}
```

DAO :
```$xslt
public interface DemoBeanDao extends BaseDao {
    // add private interface methods
}

@Repository("demoBeanDao")
public class DemoBeanDaoImpl extends AbstractBaseDaoImpl implements DemoBeanDao {
    
    @Override
    public Class getObjectClass() {
    	return DemoBean.class;
    }
    
    // add private methods 
}
```

Service:
```$xslt
public interface DemoBeanService extends BaseService {
    // add private interface methods
}

@Service("demoBeanService")
public class DemoBeanServiceImpl extends AbstractBaseServiceImpl implements DemoBeanService {

	@Autowired
	private DemoBeanDao demoBeanDao;
	
	@Override
	public BaseDao getDao() {
		return demoBeanDao;
	}
    
	// add private methods
}
```

Then you can use the common methods with this service.


