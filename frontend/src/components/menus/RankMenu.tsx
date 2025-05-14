import { Menu, Button } from '@mantine/core';

// Not implemented yet

export default function RankMenu() {
    return (
        <Menu shadow="md" width={60}>
            <Menu.Target>
                <Button 
                    variant="outline" 
                    color='white' 
                    styles={{
                        root: {
                            pointerEvents: 'none'
                        }
                    }}
                >
                    CHALLENGER
                </Button>
            </Menu.Target>
        </Menu>
    )
}
