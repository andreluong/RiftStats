import { Menu, Button } from '@mantine/core';
import { RegionCode, regions } from '../../types';

export default function RegionMenu({
    selectedRegionCode, 
    setSelectedRegionCode
}: {
    selectedRegionCode: RegionCode;
    setSelectedRegionCode: (region: RegionCode) => void;
}) {
    return (
        <Menu shadow="md" width={170}>
            <Menu.Target>
                <Button 
                    variant="outline" 
                    color='white' 
                    styles={{
                        root: {
                            width: 80,
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap',
                        },
                    }}
                >
                    {regions[selectedRegionCode].toUpperCase()}
                </Button>
            </Menu.Target>
            <Menu.Dropdown>
                {Object.entries(regions).map(([key, value]) => (
                    <Menu.Item key={key} onClick={() => setSelectedRegionCode(key as RegionCode)}>
                        {value.toUpperCase()}
                    </Menu.Item>
                ))}
            </Menu.Dropdown>
        </Menu>
    )
}
