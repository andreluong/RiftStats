import { Menu, Button } from '@mantine/core';

// Not implemented yet

export default function QueueMenu() {
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
                    SOLO/DUO
                </Button>
            </Menu.Target>
        </Menu>
    )
}
